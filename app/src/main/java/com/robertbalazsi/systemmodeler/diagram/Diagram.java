package com.robertbalazsi.systemmodeler.diagram;

import com.google.common.collect.Sets;
import com.robertbalazsi.systemmodeler.global.PaletteItemRegistry;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The diagram containing the objects of the system.
 */
public class Diagram extends Pane {

    //TODO observable collection?
    private Set<CanvasItem> selection = Sets.newHashSet();
    private Map<CanvasItem, InitialState> initialStateMap = new HashMap<>();
    private boolean rubberBandSelect = false;
    private boolean isMultiMove = false;
    private double rubberBandInitX, rubberBandInitY;
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
            // We clean the selection if the mouse pointer wasn't in any of the items' bounds.
            if (!mousePointerInAnyCanvasItem(event.getX(), event.getY())) {
                clearSelection();
            }
            event.consume();
        });
    }

    public void select(CanvasItem item) {
        item.select();
        selection.add(item);
    }

    public void deselect(CanvasItem item) {
        item.deselect();
        selection.remove(item);
        initialStateMap.remove(item);
    }

    public boolean isSelected(CanvasItem item) {
        return selection.contains(item);
    }

    public Set<CanvasItem> allSelected() {
        return selection;
    }

    public void clearSelection() {
        // Also hide the borders of the items
        selection.forEach(CanvasItem::deselect);
        selection.clear();
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
                CanvasItem item = PaletteItemRegistry.getItem(dragboard.getString());
                if (item != null) {
                    installItemEventHandlers(item);
                    item.relocate(event.getX(), event.getY());

                    getChildren().add(item);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void installItemEventHandlers(CanvasItem item) {
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
                selection.forEach(selectedItem -> {
                    initialStateMap.put(selectedItem, new InitialState(
                            mouseEvent.getSceneX(),
                            mouseEvent.getSceneY(),
                            selectedItem.getTranslateX(),
                            selectedItem.getTranslateY()
                    ));
                });
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
                    CanvasItem selectedItem = entry.getKey();
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
    }

    private void setupRubberBandSelection() {
        this.setOnMousePressed(event -> {
            rubberBandSelect = true;
            rubberBandInitX = event.getX();
            rubberBandInitY = event.getY();

            rubberBandRect = new Rectangle(rubberBandInitX, rubberBandInitY, 0, 0);
            rubberBandRect.setStroke(Color.BLUE);
            rubberBandRect.setStrokeWidth(1);
            rubberBandRect.setStrokeLineCap(StrokeLineCap.ROUND);
            rubberBandRect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.4));
            getChildren().add(rubberBandRect);

            event.consume();
        });

        this.setOnMouseDragged(event -> {
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

            event.consume();
        });

        this.setOnMouseReleased(event -> {
            if (rubberBandSelect) {
                if( !event.isShiftDown() && !event.isControlDown()) {
                    clearSelection();
                }
                for (Node item : getChildren().stream().filter(node -> node instanceof CanvasItem).collect(Collectors.toList())) {
                    CanvasItem diagramItem = (CanvasItem)item;
                    if (diagramItem.getBoundsInParent().intersects(rubberBandRect.getBoundsInParent())) {
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
            event.consume();
        });
    }

    private static class InitialState {
        private final double initMouseX;
        private final double initMouseY;
        private final double initTranslateX;
        private final double initTranslateY;

        public InitialState(final double initMouseX, final double initMouseY, final double initTranslateX, final double initTranslateY) {
            this.initMouseX = initMouseX;
            this.initMouseY = initMouseY;
            this.initTranslateX = initTranslateX;
            this.initTranslateY = initTranslateY;
        }
    }
}
