package com.robertbalazsi.systemmodeler.canvas;

import com.google.common.collect.Sets;
import com.robertbalazsi.systemmodeler.global.PaletteItemRegistry;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The canvas containing the objects of the system.
 */
public class ObjectCanvas extends Pane {

    //TODO observable collection?
    private Set<CanvasItem> selection = Sets.newHashSet();
    private boolean rubberBandSelect = false;
    private double rubberBandInitX, rubberBandInitY;
    private Rectangle rubberBandRect;

    public ObjectCanvas() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/objectCanvas.fxml"));
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
//            if (rubberBandSelect) {
//                rubberBandSelect = false;
//                event.consume();
//                return;
//            }
            // We clean the selection if the mouse pointer wasn't in any of the items' bounds.
            if (!mousePointerInAnyCanvasItem(event.getX(), event.getY())) {
                clearSelection();
            }
            event.consume();
        });
    }

    public void select(CanvasItem item) {
        item.showBoundedBox();
        selection.add(item);
    }

    public void deselect(CanvasItem item) {
        item.hideBoundedBox();
        selection.remove(item);
    }

    public boolean isSelected(CanvasItem item) {
        return selection.contains(item);
    }

    public void clearSelection() {
        // Also remove all bounded boxes from the canvas
        selection.forEach(CanvasItem::hideBoundedBox);
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

        //TODO: provide visual feedback by implementing onDragEntered() and onDragExited()

        this.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasString()) {
                Shape paletteItem = PaletteItemRegistry.getItem(dragboard.getString());
                if (paletteItem != null) {
                    paletteItem.relocate(event.getX(), event.getY());

                    //TODO: decide on the domain object (use explicit objects for components and sub-systems)
                    getChildren().add(new CanvasItem(this, paletteItem, null));
                    success = true;
                }
            }
            event.setDropCompleted(success);
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
            rubberBandRect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.6));
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
                    CanvasItem canvasItem = (CanvasItem)item;
                    if (canvasItem.getBoundsInParent().intersects(rubberBandRect.getBoundsInParent())) {
                        if (event.isShiftDown()) {
                            select(canvasItem);
                        } else if (event.isControlDown()) {
                            if (isSelected(canvasItem)) {
                                deselect(canvasItem);
                            } else {
                                select(canvasItem);
                            }
                        } else {
                            select(canvasItem);
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
}
