package com.robertbalazsi.systemmodeler.canvas;

import com.robertbalazsi.systemmodeler.domain.Entity;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import lombok.Getter;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Encapsulates an object on the canvas, which includes its shape and its underlying domain object;
 */
public class CanvasItem {

    @Getter
    private final Shape visual;

    @Getter
    private final Entity domain;

    @Getter
    private final BoundedBox boundedBox;

    public CanvasItem(final Shape visual, final Entity domain) {
        this.visual = visual;
        this.domain = domain;

        Bounds bounds = visual.getBoundsInParent();
        boundedBox = new BoundedBox(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());

        applyMover(visual);

        //TODO: fix it
//        applyMover(boundedBox);
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

    private static void applyMover(Node node) {
        Bounds initialBounds = node.getBoundsInParent();

        node.setOnMouseDragged(event -> {
            node.setTranslateX(event.getSceneX() - initialBounds.getMinX());
            node.setTranslateY(event.getSceneY() - initialBounds.getMinY());
        });
    }
}
