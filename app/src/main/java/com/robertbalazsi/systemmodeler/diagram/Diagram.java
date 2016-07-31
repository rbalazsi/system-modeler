package com.robertbalazsi.systemmodeler.diagram;

import com.google.common.collect.Sets;
import com.robertbalazsi.systemmodeler.global.PaletteItemRegistry;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The diagram containing the objects of the system.
 */
public class Diagram extends Pane {

    //TODO observable collection?
    private Set<DiagramItem> selection = Sets.newHashSet();
    private boolean rubberBandSelect = false;
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
            //TODO: review/solve the focus problem
            this.setFocused(true);
            // We clean the selection if the mouse pointer wasn't in any of the items' bounds.
            if (!mousePointerInAnyCanvasItem(event.getX(), event.getY())) {
                clearSelection();
            }
            event.consume();
        });
        this.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.DELETE) {
                    //TODO: implement
                    System.out.println("Delete pressed");
                }
            }
        });
    }

    public void select(DiagramItem item) {
        item.select();
        selection.add(item);
    }

    public void deselect(DiagramItem item) {
        item.deselect();
        selection.remove(item);
    }

    public boolean isSelected(DiagramItem item) {
        return selection.contains(item);
    }

    public Set<DiagramItem> allSelected() {
        return selection;
    }

    public void clearSelection() {
        // Also hide the borders of the items
        selection.forEach(DiagramItem::deselect);
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
                Shape paletteItem = PaletteItemRegistry.getItem(dragboard.getString());
                if (paletteItem != null) {
                    DiagramItem item = new DiagramItem(this, paletteItem, null);
                    item.relocate(event.getX(), event.getY());

                    getChildren().add(item);
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
                for (Node item : getChildren().stream().filter(node -> node instanceof DiagramItem).collect(Collectors.toList())) {
                    DiagramItem diagramItem = (DiagramItem)item;
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
}