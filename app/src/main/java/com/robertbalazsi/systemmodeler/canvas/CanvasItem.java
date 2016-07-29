package com.robertbalazsi.systemmodeler.canvas;

import com.robertbalazsi.systemmodeler.domain.Entity;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import lombok.Getter;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Encapsulates an object on the canvas, which includes its shape and its underlying domain object;
 */
public class CanvasItem extends Group {

    private double initialMouseX, initialMouseY;
    private double origTranslateX, origTranslateY;
    private boolean isMoving = false;

    @Getter
    private final ObjectCanvas parentCanvas;

    @Getter
    private final Shape visual;

    @Getter
    private final Entity domain;

    @Getter
    private final BoundedBox boundedBox;

    public CanvasItem(final ObjectCanvas parentCanvas, final Shape visual, final Entity domain) {
        this.parentCanvas = parentCanvas;
        this.visual = visual;
        this.domain = domain;

        Bounds bounds = visual.getBoundsInParent();
        boundedBox = new BoundedBox(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
        boundedBox.setVisible(false);

        applyMover(visual);

        visual.setOnMouseClicked(clickedEvent -> {
            if (isMoving) {
                isMoving = false;
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
                //TODO: property page update
            }
            clickedEvent.consume();
        });

        getChildren().add(visual);
        getChildren().add(boundedBox);
    }

    public void showBoundedBox() {
        boundedBox.setVisible(true);
    }

    public void hideBoundedBox() {
        boundedBox.setVisible(false);
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
        //TODO: implement group move
        node.setOnMousePressed(event -> {
            initialMouseX = event.getSceneX();
            initialMouseY = event.getSceneY();
            origTranslateX = visual.getTranslateX();
            origTranslateY = visual.getTranslateY();
            event.consume();
        });

        node.setOnMouseDragged(event -> {
            isMoving = true;
            double offsetX = event.getSceneX() - initialMouseX;
            double offsetY = event.getSceneY() - initialMouseY;
            double newTranslateX = origTranslateX + offsetX;
            double newTranslateY = origTranslateY + offsetY;

            visual.setTranslateX(newTranslateX);
            visual.setTranslateY(newTranslateY);

            boundedBox.setTranslateX(newTranslateX);
            boundedBox.setTranslateY(newTranslateY);
            event.consume();
        });
    }
}
