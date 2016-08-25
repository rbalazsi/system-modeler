package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.command.AddItemsCommand;
import com.robertbalazsi.systemmodeler.command.Command;
import com.robertbalazsi.systemmodeler.command.CompoundCommand;
import com.robertbalazsi.systemmodeler.command.CopyToClipboardCommand;
import com.robertbalazsi.systemmodeler.command.DeleteItemsCommand;
import com.robertbalazsi.systemmodeler.command.PasteItemsCommand;
import com.robertbalazsi.systemmodeler.command.PropertyChangeCommand;
import com.robertbalazsi.systemmodeler.command.RelocateItemsCommand;
import com.robertbalazsi.systemmodeler.command.ResizeItemCommand;
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
import lombok.Getter;

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
    private static final double RUBBER_BAND_SELECT_THRESHOLD = 5.0;

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

    private Map<DiagramItem, ItemState> itemStateMap = new HashMap<>();
    private Map<String, ItemCoord> selectedItemsPositionDeltas = new HashMap<>();
    private List<DiagramItem> dragCopyItems = new ArrayList<>();
    private boolean rubberBandSelect = false;
    private boolean isMultiMove = false;
    private boolean isItemEditing = false;
    private boolean isDragCopying = false;
    private double rubberBandInitX, rubberBandInitY;
    private double mouseX, mouseY;
    private Bounds initBounds;
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
    }

    public void addItem(DiagramItem item) {
        DiagramItemRegistry.putItem(item);
        installItemEventHandlers(item);
        getChildren().add(item);
    }

    public void removeItems(List<DiagramItem> items) {
        for (DiagramItem nextItem : items) {
            DiagramItemRegistry.removeItem(nextItem.getId());
            this.getChildren().remove(nextItem);
        }
    }

    public void selectItems(Collection<DiagramItem> items) {
        for (DiagramItem item : items) {
            item.setSelected(true);
            item.requestFocus();
            selectedItems.add(item);
        }
    }

    public void deselectItems(Collection<DiagramItem> items) {
        for (DiagramItem item : items) {
            item.setSelected(false);
            selectedItems.remove(item);
            itemStateMap.remove(item);
        }
    }

    public boolean isSelected(DiagramItem item) {
        return selectedItems.contains(item);
    }

    public void clearSelection() {
        // Also hide the borders of the items
        selectedItems.forEach(item -> item.setSelected(false));
        selectedItems.clear();
        itemStateMap.clear();
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
            selectedItemsPositionDeltas.put(item.getId(), new ItemCoord(item.getLayoutX() - deltaX,
                    item.getLayoutY() - deltaY));
        }
        ClipboardContent content = new ClipboardContent();
        content.putString(CLIPBOARD_ITEMS_PREFIX + joiner.toString());

        Command command = new CompoundCommand(Arrays.asList(
                new CopyToClipboardCommand(content),
                new PropertyChangeCommand<>(itemsCopied, true)
        ));
        ChangeManager.getInstance().putCommand(command);
        command.execute();
    }

    public void pasteItems(boolean center) {
        Bounds bounds = getLayoutBounds();
        double fromX = !center ? mouseX : (bounds.getMaxX() - bounds.getMinX()) / 2;
        double fromY = !center ? mouseY : (bounds.getMaxY() - bounds.getMinY()) / 2;

        Clipboard clipboard = Clipboard.getSystemClipboard();
        String contents = clipboard.getString();
        String[] ids = contents.substring(CLIPBOARD_ITEMS_PREFIX.length(), contents.length()).split(",");
        Map<String, ItemCoord> itemCoords = new HashMap<>();
        for (String id : ids) {
            ItemCoord delta = selectedItemsPositionDeltas.get(id);
            itemCoords.put(id, new ItemCoord(fromX + delta.x, fromY + delta.y));
        }

        Command pasteCommand = new PasteItemsCommand(this, itemCoords);
        ChangeManager.getInstance().putCommand(pasteCommand);
        pasteCommand.execute();
    }

    public void deleteSelected() {
        Command command = new CompoundCommand(Arrays.asList(
                new DeleteItemsCommand(this, new ArrayList<>(selectedItems)),
                new PropertyChangeCommand<>(itemsCopied, false)
        ));
        ChangeManager.getInstance().putCommand(command);
        command.execute();
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
                    item.relocate(event.getX(), event.getY());
                    Command addCommand = new AddItemsCommand(this, Arrays.asList(item));
                    ChangeManager.getInstance().putCommand(addCommand);
                    addCommand.execute();
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void installItemEventHandlers(DiagramItem item) {
        item.addEventHandler(DiagramItemMouseEvent.SELECTED, event -> {
            MouseEvent mouseEvent = event.getMouseEvent();
            Command selectionCommand = null;

            List<DiagramItem> selectedItems = new ArrayList<>();
            List<DiagramItem> deselectedItems = new ArrayList<>();
            if (!mouseEvent.isShiftDown() && !mouseEvent.isControlDown()) {
                deselectedItems.addAll(this.selectedItems);
                selectedItems.add(item);
            } else if (mouseEvent.isControlDown() && isSelected(item)) {
                deselectedItems.add(item);
            } else if (!isSelected(item)) {
                selectedItems.add(item);
            }

            if (!selectedItems.isEmpty() || !deselectedItems.isEmpty()) {
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
                itemStateMap.put(item, new ItemState(
                        mouseEvent.getSceneX(),
                        mouseEvent.getSceneY(),
                        item.getTranslateX(),
                        item.getTranslateY()
                ));
            }
            // Move all selected items
            else {
                selectedItems.forEach(selectedItem -> itemStateMap.put(selectedItem, new ItemState(
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

                Collection<DiagramItem> itemsToMove = !isMultiMove ? Collections.singletonList(item) : itemStateMap.keySet();

                itemsToMove.forEach(itemToMove -> {
                    ItemState initState = itemStateMap.get(itemToMove);
                    double currentTranslateX = initState.initTranslateX + mouseEvent.getSceneX() - initState.initMouseX;
                    double currentTranslateY = initState.initTranslateY + mouseEvent.getSceneY() - initState.initMouseY;
                    initState.currentTranslateX = currentTranslateX;
                    initState.currentTranslateY = currentTranslateY;

                    itemToMove.setTranslateX(currentTranslateX);
                    itemToMove.setTranslateY(currentTranslateY);
                });
            }
            event.consume();
        });
        item.addEventHandler(DiagramItemMouseEvent.MOVE_FINISHED, event -> {
            setCursor(Cursor.DEFAULT);
            isMultiMove = false;
            Map<String, ItemCoord> itemDeltas = new HashMap<>();
            for (Map.Entry<DiagramItem, ItemState> itemState : itemStateMap.entrySet()) {
                ItemState state = itemState.getValue();
                itemDeltas.put(itemState.getKey().getId(), new ItemCoord(state.currentTranslateX, state.currentTranslateY));
            }

            // We are registering the command without running it because the relocation is already done at this point
            ChangeManager.getInstance().putCommand(new RelocateItemsCommand(itemDeltas));

            itemStateMap.clear();
            event.consume();
        });
        item.addEventHandler(DiagramItemMouseEvent.RESIZE_STARTED, event -> {
            initBounds = item.getBoundsInParent();
            event.consume();
        });
        item.addEventHandler(DiagramItemMouseEvent.RESIZE_FINISHED, event -> {
            // We are registering the command without running it because the resizing is already done at this point
            ChangeManager.getInstance().putCommand(new ResizeItemCommand(item, initBounds, item.getBoundsInParent()));
            event.consume();
        });
        item.addEventHandler(DiagramItemMouseEvent.DRAG_COPY_STARTED, event -> {
            if (selectedItems.isEmpty()) {
                dragCopyItems.add(item.copy());
            } else {
                dragCopyItems.addAll(selectedItems.stream().map(DiagramItem::copy).collect(Collectors.toList()));
            }
            itemStateMap.clear();
            MouseEvent mouseEvent = event.getMouseEvent();
            dragCopyItems.forEach(copyItem -> {
                DiagramItemRegistry.putItem(copyItem);
                getChildren().add(copyItem);
                itemStateMap.put(copyItem, new ItemState(
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
            itemStateMap.entrySet().forEach(entry -> {
                DiagramItem currentItem = entry.getKey();
                ItemState initState = entry.getValue();

                currentItem.setTranslateX(initState.initTranslateX + mouseEvent.getSceneX() - initState.initMouseX);
                currentItem.setTranslateY(initState.initTranslateY + mouseEvent.getSceneY() - initState.initMouseY);
            });

            event.consume();
        });
        item.addEventHandler(DiagramItemMouseEvent.DRAG_COPY_FINISHED, event -> {
            isDragCopying = false;

            // We are just registering the command here, not executing it because items are already copied at this point
            ChangeManager.getInstance().putCommand(new AddItemsCommand(this, dragCopyItems));

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
                rubberBandInitX = event.getX();
                rubberBandInitY = event.getY();
            }
            event.consume();
        });

        this.setOnMouseDragged(event -> {
            if (event.getButton() != MouseButton.SECONDARY) {
                double offsetX = event.getX() - rubberBandInitX;
                double offsetY = event.getY() - rubberBandInitY;

                if (!rubberBandSelect) {
                    if (offsetX > RUBBER_BAND_SELECT_THRESHOLD || offsetY > RUBBER_BAND_SELECT_THRESHOLD) {
                        rubberBandSelect = true;
                        rubberBandRect = new Rectangle(rubberBandInitX, rubberBandInitY, 0, 0);
                        rubberBandRect.setStroke(Color.GRAY);
                        rubberBandRect.setStrokeWidth(0.5);
                        rubberBandRect.getStrokeDashArray().addAll(6.0);
                        rubberBandRect.setStrokeLineCap(StrokeLineCap.ROUND);
                        rubberBandRect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.3));
                        getChildren().add(rubberBandRect);
                    }
                } else {
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

    private static class ItemState {
        private final double initMouseX;
        private final double initMouseY;
        private final double initTranslateX;
        private final double initTranslateY;
        private double currentTranslateX;
        private double currentTranslateY;

        ItemState(final double initMouseX, final double initMouseY, final double initTranslateX, final double initTranslateY) {
            this.initMouseX = initMouseX;
            this.initMouseY = initMouseY;
            this.initTranslateX = initTranslateX;
            this.initTranslateY = initTranslateY;
        }
    }

    public static class ItemCoord {
        @Getter
        private final double x;

        @Getter
        private final double y;

        ItemCoord(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
