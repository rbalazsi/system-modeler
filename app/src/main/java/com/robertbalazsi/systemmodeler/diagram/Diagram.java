package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.command.Command;
import com.robertbalazsi.systemmodeler.command.DeleteCommand;
import com.robertbalazsi.systemmodeler.command.SelectionChangeCommand;
import com.robertbalazsi.systemmodeler.global.ChangeManager;
import com.robertbalazsi.systemmodeler.global.DiagramItemRegistry;
import com.robertbalazsi.systemmodeler.global.PaletteItemRegistry;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The diagram containing the objects of the system.
 */
public class Diagram extends Pane {

    private static TextField itemTextEditor = new TextField();
    private static ContextMenu contextMenu = new ContextMenu();
    private static final String CLIPBOARD_ITEMS_PREFIX = "_items:";

    private SetProperty<DiagramItem> selectedItems = new SimpleSetProperty<>(this, "selectedItems", FXCollections.observableSet());

    public SetProperty<DiagramItem> selectedItemsProperty() {
        return selectedItems;
    }

    public ObservableSet<DiagramItem> getSelectedItems() {
        return selectedItems.get();
    }

    private BooleanProperty itemsCopied = new SimpleBooleanProperty(this, "itemsCopied", false);

    public final BooleanProperty itemsCopiedProperty() {
        return itemsCopied;
    }

    public final boolean isItemsCopied() {
        return itemsCopied.get();
    }

    private Map<DiagramItem, InitialState> initialStateMap = new HashMap<>();
    private Map<DiagramItem, ItemCoord> selectedItemsPositionDeltas = new HashMap<>();
    private List<DiagramItem> dragCopyItems = new ArrayList<>();
    private boolean rubberBandSelect = false;
    private boolean isMultiMove = false;
    private boolean isItemEditing = false;
    private boolean isDragCopying = false;
    private double rubberBandInitX, rubberBandInitY;
    private double mouseX, mouseY;
    private Rectangle rubberBandRect;

    public Diagram() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/diagram.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setupDragDropHandlers();
        setupRubberBandSelection();

        this.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                resetContextMenu();
                if (!selectedItems.isEmpty()) {
                    contextMenu.getItems().add(copyMenuItem());
                    contextMenu.getItems().add(deleteMenuItem());
                }
                if (Clipboard.getSystemClipboard().hasString() && Clipboard.getSystemClipboard().getString().startsWith(CLIPBOARD_ITEMS_PREFIX)) {
                    contextMenu.getItems().add(pasteMenuItem());
                }
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
                event.consume();
            } else {
                // We clean the selection if the mouse pointer wasn't in any of the items' bounds.
                if (!mousePointerInAnyCanvasItem(event.getX(), event.getY())) {
                    Command clearSelectionCommand = new SelectionChangeCommand(this, Collections.emptyList(), selectedItems);
                    ChangeManager.getInstance().putCommand(clearSelectionCommand);
                    clearSelectionCommand.execute();
                }
                if (isItemEditing) {
                    getChildren().remove(itemTextEditor);
                    isItemEditing = false;
                }
            }
            event.consume();
        });

        // We are taking note of the mouse position in the scene for future events needing it.
        this.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
            event.consume();
        });

        this.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                deleteSelected();
            } else if (event.isControlDown() && event.getCode() == KeyCode.C) {
                copySelected();
            } else if (event.isControlDown() && event.getCode() == KeyCode.V) {
                pasteItems(false);
            }
            event.consume();
        });
    }

    public void addItem(DiagramItem item) {
        DiagramItemRegistry.putItem(item);
        installItemEventHandlers(item);
        getChildren().add(item);
    }

    //TODO: removeItem() to uninstall event handlers
    public void removeItems(List<DiagramItem> items) {
        Iterator<DiagramItem> itemIterator = items.iterator();
        while (itemIterator.hasNext()) {
            DiagramItem nextItem = itemIterator.next();
            DiagramItemRegistry.removeItem(nextItem.getId());
            itemIterator.remove();
            this.getChildren().remove(nextItem);
        }
    }

    public void selectItems(Collection<DiagramItem> items) {
        Iterator<DiagramItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            DiagramItem item = iterator.next();
            item.setSelected(true);
            item.requestFocus();
            selectedItems.add(item);
        }
    }

    public void deselectItems(Collection<DiagramItem> items) {
        Iterator<DiagramItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            DiagramItem item = iterator.next();
            item.setSelected(false);
            items.remove(item);
            initialStateMap.remove(item);
        }
    }

    public boolean isSelected(DiagramItem item) {
        return selectedItems.contains(item);
    }

    public void clearSelection() {
        // Also hide the borders of the items
        selectedItems.forEach(item -> item.setSelected(false));
        selectedItems.clear();
        initialStateMap.clear();
    }

    public void copySelected() {
        selectedItemsPositionDeltas.clear();
        StringJoiner joiner = new StringJoiner(",");

        // We store the position difference of each selected item relative to the center of the bounding box surrounding
        // all selected items
        double leftX = Double.MAX_VALUE, topY = Double.MAX_VALUE;
        double rightX = Double.MIN_VALUE, bottomY = Double.MIN_VALUE;
        for (DiagramItem item : selectedItems) {
            if (item.getLayoutX() < leftX) {
                leftX = item.getLayoutX();
            }
            if (item.getLayoutX() + item.getWidth() > rightX) {
                rightX = item.getLayoutX() + item.getWidth();
            }
            if (item.getLayoutY() < topY) {
                topY = item.getLayoutY();
            }
            if (item.getLayoutY() + item.getHeight() > bottomY) {
                bottomY = item.getLayoutY() + item.getHeight();
            }
        }

        double deltaX = leftX + (rightX - leftX) / 2;
        double deltaY = topY + (bottomY - topY) / 2;
        for (DiagramItem item : selectedItems) {
            joiner.add(item.getId());
            selectedItemsPositionDeltas.put(item, new ItemCoord(item.getLayoutX() - deltaX,
                    item.getLayoutY() - deltaY));
        }
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(CLIPBOARD_ITEMS_PREFIX + joiner.toString());
        clipboard.setContent(content);
        itemsCopied.set(true);
    }

    public void pasteItems(boolean center) {
        Bounds bounds = getLayoutBounds();
        double fromX = !center ? mouseX : (bounds.getMaxX() - bounds.getMinX()) / 2;
        double fromY = !center ? mouseY : (bounds.getMaxY() - bounds.getMinY()) / 2;

        Clipboard clipboard = Clipboard.getSystemClipboard();
        String contents = clipboard.getString();
        String[] ids = contents.substring(CLIPBOARD_ITEMS_PREFIX.length(), contents.length()).split(",");
        for (String id : ids) {
            DiagramItem item = DiagramItemRegistry.getItem(id);
            DiagramItem itemCopy = item.copy();
            ItemCoord delta = selectedItemsPositionDeltas.get(item);
            itemCopy.relocate(fromX + delta.x, fromY + delta.y);
            addItem(itemCopy);
        }
    }

    public void deleteSelected() {
        Command deleteCommand = new DeleteCommand(this, new ArrayList<>(selectedItems));
        ChangeManager.getInstance().putCommand(deleteCommand);
        deleteCommand.execute();
        itemsCopied.set(false);
    }

    public void undoLast() {
        ChangeManager.getInstance().undoLast();
    }

    public void redoLast() {
        ChangeManager.getInstance().redoLast();
    }

    private boolean mousePointerInAnyCanvasItem(double mouseX, double mouseY) {
        for (Node child : getChildren()) {
            if (child.getBoundsInParent().contains(mouseX, mouseY)) {
                return true;
            }
        }

        return false;
    }

    private void setupDragDropHandlers() {
        this.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != this) {
                    event.acceptTransferModes(TransferMode.COPY);
                }
                event.consume();
            }
        });

        this.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasString()) {
                DiagramItem item = PaletteItemRegistry.getItem(dragboard.getString());
                if (item != null) {
                    addItem(item);
                    item.relocate(event.getX(), event.getY());
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    //TODO: IMPORTANT Selection broken, retest and fix it!
    private void installItemEventHandlers(DiagramItem item) {
        item.addEventHandler(DiagramItemMouseEvent.SELECTED, event -> {
            MouseEvent mouseEvent = event.getMouseEvent();
            Command selectionCommand = null;

            Collection<DiagramItem> selectedItems = null;
            Collection<DiagramItem> deselectedItems = null;
            if (!mouseEvent.isShiftDown() && !mouseEvent.isControlDown()) {
                selectedItems = Collections.emptyList();
                deselectedItems = this.selectedItems;
            }
            if (mouseEvent.isControlDown() && isSelected(item)) {
                selectedItems = Collections.emptyList();
                deselectedItems = Collections.singletonList(item);
            } else if (!isSelected(item)) {
                selectedItems = Collections.singletonList(item);
                deselectedItems = deselectedItems.isEmpty() ? Collections.emptyList() : deselectedItems;
            }

            if (selectedItems != null && deselectedItems != null) {
                selectionCommand = new SelectionChangeCommand(this, selectedItems, deselectedItems);
                ChangeManager.getInstance().putCommand(selectionCommand);
                selectionCommand.execute();
            }

            event.consume();
        });
        item.addEventHandler(DiagramItemMouseEvent.MOVE_STARTED, event -> {
            isMultiMove = isSelected(item);
            MouseEvent mouseEvent = event.getMouseEvent();

            // Move current item
            if (!isMultiMove) {
                initialStateMap.put(item, new InitialState(
                        mouseEvent.getSceneX(),
                        mouseEvent.getSceneY(),
                        item.getTranslateX(),
                        item.getTranslateY()
                ));
            }
            // Move all selected items
            else {
                selectedItems.forEach(selectedItem -> initialStateMap.put(selectedItem, new InitialState(
                        mouseEvent.getSceneX(),
                        mouseEvent.getSceneY(),
                        selectedItem.getTranslateX(),
                        selectedItem.getTranslateY()
                )));
            }
            event.consume();
        });
        item.addEventHandler(DiagramItemMouseEvent.MOVING, event -> {
            if (!isDragCopying) {
                setCursor(Cursor.MOVE);
                MouseEvent mouseEvent = event.getMouseEvent();
                if (!isMultiMove) {
                    InitialState initState = initialStateMap.get(item);
                    item.setTranslateX(initState.initTranslateX + mouseEvent.getSceneX() - initState.initMouseX);
                    item.setTranslateY(initState.initTranslateY + mouseEvent.getSceneY() - initState.initMouseY);
                } else {
                    initialStateMap.entrySet().forEach(entry -> {
                        DiagramItem selectedItem = entry.getKey();
                        InitialState initState = entry.getValue();

                        selectedItem.setTranslateX(initState.initTranslateX + mouseEvent.getSceneX() - initState.initMouseX);
                        selectedItem.setTranslateY(initState.initTranslateY + mouseEvent.getSceneY() - initState.initMouseY);
                    });
                }
            }
            event.consume();
        });
        item.addEventHandler(DiagramItemMouseEvent.MOVE_FINISHED, event -> {
            setCursor(Cursor.DEFAULT);
            isMultiMove = false;
            initialStateMap.clear();
            event.consume();
        });

        item.addEventHandler(DiagramItemMouseEvent.DRAG_COPY_STARTED, event -> {
            if (selectedItems.isEmpty()) {
                dragCopyItems.add(item.copy());
            } else {
                dragCopyItems.addAll(selectedItems.stream().map(DiagramItem::copy).collect(Collectors.toList()));
            }
            initialStateMap.clear();
            MouseEvent mouseEvent = event.getMouseEvent();
            dragCopyItems.forEach(copyItem -> {
                DiagramItemRegistry.putItem(copyItem);
                getChildren().add(copyItem);
                initialStateMap.put(copyItem, new InitialState(
                        mouseEvent.getSceneX(),
                        mouseEvent.getSceneY(),
                        copyItem.getTranslateX(),
                        copyItem.getTranslateY()
                ));
            });
            event.consume();
        });
        item.addEventHandler(DiagramItemMouseEvent.DRAG_COPYING, event -> {
            isDragCopying = true;
            MouseEvent mouseEvent = event.getMouseEvent();
            initialStateMap.entrySet().forEach(entry -> {
                DiagramItem currentItem = entry.getKey();
                InitialState initState = entry.getValue();

                currentItem.setTranslateX(initState.initTranslateX + mouseEvent.getSceneX() - initState.initMouseX);
                currentItem.setTranslateY(initState.initTranslateY + mouseEvent.getSceneY() - initState.initMouseY);
            });

            event.consume();
        });
        item.addEventHandler(DiagramItemMouseEvent.DRAG_COPY_FINISHED, event -> {
            isDragCopying = false;
            dragCopyItems.forEach(this::installItemEventHandlers);
            dragCopyItems.clear();
            event.consume();
        });
        item.addEventHandler(DiagramItemMouseEvent.DRAG_COPY_CANCELLED, event -> {
            isDragCopying = false;
            removeItems(dragCopyItems);
            event.consume();
        });

        item.addEventHandler(DiagramItemMouseEvent.TEXT_EDITING, event -> {
            isItemEditing = true;
            itemTextEditor.setOnAction(actionEvent -> {
                String enteredText = itemTextEditor.getText();
                getChildren().remove(itemTextEditor);
                item.setText(enteredText);
                isItemEditing = false;
            });
            itemTextEditor.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    getChildren().remove(itemTextEditor);
                    isItemEditing = false;
                }
            });
            itemTextEditor.relocate(item.getLayoutX() + item.getWidth() / 2, item.getLayoutY() + item.getHeight() / 2);
            getChildren().add(itemTextEditor);
            itemTextEditor.requestFocus();
        });
        item.addEventHandler(DiagramItemMouseEvent.CONTEXT_MENU, event -> {
            resetContextMenu();
            if (!selectedItems.isEmpty()) {
                contextMenu.getItems().add(copyMenuItem());
                contextMenu.getItems().add(deleteMenuItem());
                MouseEvent mouseEvent = event.getMouseEvent();
                contextMenu.show(item, mouseEvent.getScreenX(), mouseEvent.getScreenY());
            }
            event.consume();
        });
    }

    private static void resetContextMenu() {
        contextMenu.hide();
        contextMenu.getItems().clear();
    }

    private MenuItem copyMenuItem() {
        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(event -> copySelected());
        return copy;
    }

    private MenuItem deleteMenuItem() {
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(event -> deleteSelected());
        return delete;
    }

    private MenuItem pasteMenuItem() {
        MenuItem paste = new MenuItem("Paste");
        paste.setOnAction(actionEvent -> pasteItems(false));
        return paste;
    }

    private void setupRubberBandSelection() {
        this.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.SECONDARY) {
                rubberBandSelect = true;
                rubberBandInitX = event.getX();
                rubberBandInitY = event.getY();

                rubberBandRect = new Rectangle(rubberBandInitX, rubberBandInitY, 0, 0);
                rubberBandRect.setStroke(Color.GRAY);
                rubberBandRect.setStrokeWidth(0.5);
                rubberBandRect.getStrokeDashArray().addAll(6.0);
                rubberBandRect.setStrokeLineCap(StrokeLineCap.ROUND);
                rubberBandRect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.3));
                getChildren().add(rubberBandRect);
            }
            event.consume();
        });

        this.setOnMouseDragged(event -> {
            if (event.getButton() != MouseButton.SECONDARY) {
                double offsetX = event.getX() - rubberBandInitX;
                double offsetY = event.getY() - rubberBandInitY;

                if (offsetX > 0) {
                    rubberBandRect.setWidth(offsetX);
                } else {
                    rubberBandRect.setX(event.getX());
                    rubberBandRect.setWidth(rubberBandInitX - rubberBandRect.getX());
                }

                if (offsetY > 0) {
                    rubberBandRect.setHeight(offsetY);
                } else {
                    rubberBandRect.setY(event.getY());
                    rubberBandRect.setHeight(rubberBandInitY - rubberBandRect.getY());
                }
            }
            event.consume();
        });

        this.setOnMouseReleased(event -> {
            if (event.getButton() != MouseButton.SECONDARY) {
                if (rubberBandSelect) {
                    if (!event.isShiftDown() && !event.isControlDown()) {
                        clearSelection();
                    }
                    List<DiagramItem> selectedItems = new ArrayList<>();
                    List<DiagramItem> deselectedItems = new ArrayList<>();
                    for (Node item : getChildren().stream().filter(node -> node instanceof DiagramItem).collect(Collectors.toList())) {
                        DiagramItem diagramItem = (DiagramItem) item;
                        if (rubberBandRect.getBoundsInParent().contains(diagramItem.getBoundsInParent())) {
                            if (event.isShiftDown()) {
                                selectedItems.add(diagramItem);
                            } else if (event.isControlDown()) {
                                if (isSelected(diagramItem)) {
                                    deselectedItems.add(diagramItem);
                                } else {
                                    selectedItems.add(diagramItem);
                                }
                            } else {
                                selectedItems.add(diagramItem);
                            }
                        }
                    }
                    Command selectionCommand = new SelectionChangeCommand(this, selectedItems, deselectedItems);
                    selectionCommand.execute();
                    ChangeManager.getInstance().putCommand(selectionCommand);

                    getChildren().remove(rubberBandRect);
                    rubberBandRect = null;
                    rubberBandSelect = false;
                }
            }
            event.consume();
        });
    }

    private static class InitialState {
        private final double initMouseX;
        private final double initMouseY;
        private final double initTranslateX;
        private final double initTranslateY;

        InitialState(final double initMouseX, final double initMouseY, final double initTranslateX, final double initTranslateY) {
            this.initMouseX = initMouseX;
            this.initMouseY = initMouseY;
            this.initTranslateX = initTranslateX;
            this.initTranslateY = initTranslateY;
        }
    }

    private static class ItemCoord {
        private final double x;
        private final double y;

        ItemCoord(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
