package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.global.DiagramItemRegistry;
import com.robertbalazsi.systemmodeler.global.PaletteItemRegistry;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
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

    private SetProperty<DiagramItem> selectedItems = new SimpleSetProperty<>(this, "selectedItems", FXCollections.observableSet());

    public SetProperty<DiagramItem> selectedItemsProperty() {
        return selectedItems;
    }

    public ObservableSet<DiagramItem> getSelectedItems() {
        return selectedItems.get();
    }
    private Map<DiagramItem, InitialState> initialStateMap = new HashMap<>();
    private Map<DiagramItem, ItemCoord> selectedItemsRelativeCursorPositions = new HashMap<>();
    private boolean rubberBandSelect = false;
    private boolean isMultiMove = false;
    private boolean isItemEditing = false;
    private double rubberBandInitX, rubberBandInitY;
    private double mouseSceneX, mouseSceneY;
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
                if (Clipboard.getSystemClipboard().hasString()) {
                    contextMenu.getItems().add(pasteMenuItem());
                }
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
                event.consume();
            } else {
                // We clean the selection if the mouse pointer wasn't in any of the items' bounds.
                if (!mousePointerInAnyCanvasItem(event.getX(), event.getY())) {
                    clearSelection();
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
            mouseSceneX = event.getSceneX();
            mouseSceneY = event.getSceneY();
            event.consume();
        });

        this.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                deleteItems(selectedItems);
            } else if (event.isControlDown() && event.getCode() == KeyCode.C) {
                copyItems();
            } else if (event.isControlDown() && event.getCode() == KeyCode.V) {
                pasteItems();
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
    public void removeItem(DiagramItem item) {
        DiagramItemRegistry.removeItem(item.getId());
        selectedItems.remove(item);
        getChildren().remove(item);
    }

    public void select(DiagramItem item) {
        item.setSelected(true);
        item.requestFocus();
        selectedItems.add(item);
    }

    public void deselect(DiagramItem item) {
        item.setSelected(false);
        selectedItems.remove(item);
        initialStateMap.remove(item);
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

    private void installItemEventHandlers(DiagramItem item) {
        item.addEventHandler(DiagramItemMouseEvent.SELECTED, event -> {
            MouseEvent mouseEvent = event.getMouseEvent();
            if (!mouseEvent.isShiftDown() && !mouseEvent.isControlDown()) {
                clearSelection();
            }
            if (mouseEvent.isControlDown() && isSelected(item)) {
                deselect(item);
            } else {
                if (!isSelected(item)) {
                    select(item);
                }
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
            event.consume();
        });
        item.addEventHandler(DiagramItemMouseEvent.MOVE_FINISHED, event -> {
            setCursor(Cursor.DEFAULT);
            isMultiMove = false;
            initialStateMap.clear();
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
        copy.setOnAction(event -> copyItems());
        return copy;
    }

    private void copyItems() {
        //TODO: review the data format; we don't want arbitrary strings to be copied from other apps
        // Currently we separate the IDs of selected items with comma. This may be subject of refactoring for a more
        // elaborate serialized format (e.g. XML)
        selectedItemsRelativeCursorPositions.clear();
        StringJoiner joiner = new StringJoiner(",");
        selectedItems.forEach(item -> {
            joiner.add(item.getId());
            selectedItemsRelativeCursorPositions.put(item, new ItemCoord(mouseSceneX - item.getLayoutX(),
                    mouseSceneY - item.getLayoutY()));
        });
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(joiner.toString());
        clipboard.setContent(content);
    }

    private MenuItem deleteMenuItem() {
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(event -> deleteItems(selectedItems));
        return delete;
    }

    private void deleteItems(Set<DiagramItem> items) {
        Iterator<DiagramItem> itemsIter = items.iterator();
        while (itemsIter.hasNext()) {
            DiagramItem nextSelected = itemsIter.next();
            itemsIter.remove();
            getChildren().remove(nextSelected);
        }
    }

    private MenuItem pasteMenuItem() {
        MenuItem paste = new MenuItem("Paste");
        paste.setOnAction(actionEvent -> pasteItems());
        return paste;
    }

    private void pasteItems() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String[] ids = clipboard.getString().split(",");
        // We paste the new item in the cursor position
        if (ids.length == 1) {
            DiagramItem item = DiagramItemRegistry.getItem(ids[0]);
            DiagramItem itemCopy = item.copy();
            itemCopy.relocate(mouseSceneX, mouseSceneY);
            addItem(itemCopy);
        }
        // We paste all items in their relative positions to the cursor
        else {
            for (String id : ids) {
                DiagramItem item = DiagramItemRegistry.getItem(id);
                DiagramItem itemCopy = item.copy();
                ItemCoord delta = selectedItemsRelativeCursorPositions.get(item);
                itemCopy.relocate(mouseSceneX - delta.x, mouseSceneY - delta.y);
                addItem(itemCopy);
            }
        }
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
                    for (Node item : getChildren().stream().filter(node -> node instanceof DiagramItem).collect(Collectors.toList())) {
                        DiagramItem diagramItem = (DiagramItem) item;
                        if (rubberBandRect.getBoundsInParent().contains(diagramItem.getBoundsInParent())) {
                            if (event.isShiftDown()) {
                                select(diagramItem);
                            } else if (event.isControlDown()) {
                                if (isSelected(diagramItem)) {
                                    deselect(diagramItem);
                                } else {
                                    select(diagramItem);
                                }
                            } else {
                                select(diagramItem);
                            }
                        }
                    }
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
