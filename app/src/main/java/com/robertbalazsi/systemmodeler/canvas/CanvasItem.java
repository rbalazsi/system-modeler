package com.robertbalazsi.systemmodeler.canvas;

import com.google.common.collect.Maps;
import com.robertbalazsi.systemmodeler.domain.Entity;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Shape;
import lombok.Getter;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;

/**
 * Encapsulates an object on the canvas, which includes its shape and its underlying domain object;
 */
public class CanvasItem extends AnchorPane {

    private Map<CanvasItem, ItemOrigTranslate> itemOrigTranslateMap = Maps.newHashMap();

    private double initialMouseX, initialMouseY;
    private double origTranslateX, origTranslateY;
    private boolean isMoving = false;
    private boolean isMultiMove = false;

    @Getter
    private final ObjectCanvas parentCanvas;

    @Getter
    private final Shape visual;

    @Getter
    private final Entity domain;

    public CanvasItem(final ObjectCanvas parentCanvas, final Shape visual, final Entity domain) {
        this.parentCanvas = parentCanvas;
        this.visual = visual;
        this.domain = domain;

        applyMover(visual);

        visual.setOnMouseClicked(clickedEvent -> {
            if (isMoving) {
                isMoving = false;
                isMultiMove = false;
                itemOrigTranslateMap.clear();
                clickedEvent.consume();
                return;
            }
            if (!clickedEvent.isShiftDown() && !clickedEvent.isControlDown()) {
                parentCanvas.clearSelection();
            }
            if (clickedEvent.isControlDown() && parentCanvas.isSelected(this)) {
                parentCanvas.deselect(this);
            } else {
                if (!parentCanvas.isSelected(this)) {
                    parentCanvas.select(this);
                }
            }
            clickedEvent.consume();
        });

        getChildren().add(visual);

        deselect();
        AnchorPane.setBottomAnchor(visual, 1.0);
        AnchorPane.setRightAnchor(visual, 1.0);
        AnchorPane.setTopAnchor(visual, 1.0);
        AnchorPane.setLeftAnchor(visual, 1.0);
    }

    void select() {
        this.setStyle("-fx-border-style: dashed; -fx-border-color: blue");
    }

    void deselect() {
        this.setStyle("-fx-border-color: transparent");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CanvasItem rhs = (CanvasItem) o;

        // There is no need to consider the domain entity as no two different canvas item shapes can share the same domain
        // object. The bounded box is also not taken into account.
        return this.equals(rhs);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(visual).hashCode();
    }

    private void applyMover(Node node) {
        node.setOnMousePressed(event -> {
            isMultiMove = parentCanvas.isSelected(this);
            initialMouseX = event.getSceneX();
            initialMouseY = event.getSceneY();

            // Move current item
            if (!isMultiMove) {
                origTranslateX = getTranslateX();
                origTranslateY = getTranslateY();
            }
            // Move all selected items of parent canvas
            else {
                for (CanvasItem item : parentCanvas.allSelected()) {
                    itemOrigTranslateMap.put(item, new ItemOrigTranslate(item.getTranslateX(), item.getTranslateY()));
                }
            }
            event.consume();
        });

        node.setOnMouseDragged(event -> {
            isMoving = true;
            setCursor(Cursor.MOVE);
            double offsetX = event.getSceneX() - initialMouseX;
            double offsetY = event.getSceneY() - initialMouseY;
            if (!isMultiMove) {
                double newTranslateX = origTranslateX + offsetX;
                double newTranslateY = origTranslateY + offsetY;

                setTranslateX(newTranslateX);
                setTranslateY(newTranslateY);
            } else {
                for (Map.Entry<CanvasItem, ItemOrigTranslate> entry : itemOrigTranslateMap.entrySet()) {
                    CanvasItem item = entry.getKey();
                    ItemOrigTranslate itemTranslate = entry.getValue();

                    double newTranslateX = itemTranslate.origTranslateX + offsetX;
                    double newTranslateY = itemTranslate.origTranslateY + offsetY;

                    item.setTranslateX(newTranslateX);
                    item.setTranslateY(newTranslateY);
                }
            }
            event.consume();
        });
        node.setOnMouseReleased(event -> {
            setCursor(Cursor.DEFAULT);
        });
    }

    private static class ItemOrigTranslate {
        double origTranslateX;
        double origTranslateY;

        ItemOrigTranslate(double origTranslateX, double origTranslateY) {
            this.origTranslateX = origTranslateX;
            this.origTranslateY = origTranslateY;
        }
    }
}
