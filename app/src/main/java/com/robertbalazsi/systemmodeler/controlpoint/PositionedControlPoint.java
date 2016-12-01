package com.robertbalazsi.systemmodeler.controlpoint;

import com.robertbalazsi.systemmodeler.diagram.Visual;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lombok.Getter;

/**
 * A control point placed in a predefined position in the parent.
 * //TODO: document on positions, etc.
 */
public abstract class PositionedControlPoint extends ControlPoint {

    @Getter
    protected Position position;

    protected PositionedControlPoint(Visual parent, Position position, boolean moveConstrained, double size, Color selectedColor, Color deselectedColor) {
        super(parent, moveConstrained, size, selectedColor, deselectedColor);
        this.position = position;
    }

    public static class Builder extends ControlPoint.Builder<Builder, PositionedControlPoint> {

        protected Position position;

        public Builder(Visual parent, Position position) {
            super(parent);
            this.position = position;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public PositionedControlPoint build() {
            return Factory.create(this);
        }
    }

    /**
     * Hidden factory class for deciding the {@link ControlPoint} implementation with respect to its position.
     */
    private static class Factory {
        static PositionedControlPoint create(Builder builder) {
            switch (builder.position) {
                case TOP_LEFT:
                    return new TopLeft(builder.parent, builder.moveConstrained, builder.size, builder.selectedColor, builder.deselectedColor);
                case TOP_CENTER:
                    return new TopCenter(builder.parent, builder.size, builder.selectedColor, builder.deselectedColor);
                case TOP_RIGHT:
                    return new TopRight(builder.parent, builder.moveConstrained, builder.size, builder.selectedColor, builder.deselectedColor);
                case MIDDLE_LEFT:
                    return new MiddleLeft(builder.parent, builder.size, builder.selectedColor, builder.deselectedColor);
                case MIDDLE_RIGHT:
                    return new MiddleRight(builder.parent, builder.size, builder.selectedColor, builder.deselectedColor);
                case BOTTOM_LEFT:
                    return new BottomLeft(builder.parent, builder.moveConstrained, builder.size, builder.selectedColor, builder.deselectedColor);
                case BOTTOM_CENTER:
                    return new BottomCenter(builder.parent, builder.size, builder.selectedColor, builder.deselectedColor);
                case BOTTOM_RIGHT:
                    return new BottomRight(builder.parent, builder.moveConstrained, builder.size, builder.selectedColor, builder.deselectedColor);
                default:
                    return null;
            }
        }
    }

    /**
     * Top left resizer control point.
     */
    private static class TopLeft extends PositionedControlPoint {

        TopLeft(Visual parent, boolean moveConstrained, double size, Color selectedColor, Color deselectedColor) {
            super(parent, Position.TOP_LEFT, moveConstrained, size, selectedColor, deselectedColor);
        }

        @Override
        public void receiveMouseDragged(MouseEvent event) {
            parent.setTranslateX(initTranslateX + event.getSceneX() - initMouseX);
            parent.setWidth(initWidth + initMouseX - event.getSceneX());

            if (!isMoveConstrained()) {
                parent.setTranslateY(initTranslateY + event.getSceneY() - initMouseY);
                parent.setHeight(initHeight + initMouseY - event.getSceneY());
            } else {
                parent.setTranslateY(initTranslateX + event.getSceneX() - initMouseX);
                parent.setHeight(initHeight + initMouseX - event.getSceneX());
            }
        }

        @Override
        public Bounds calculateBounds() {
            return new BoundingBox(0, 0, getSize(), getSize());
        }
    }

    /**
     * Top center resizer control point.
     */
    private static class TopCenter extends PositionedControlPoint {

        TopCenter(Visual parent, double size, Color selectedColor, Color deselectedColor) {
            super(parent, Position.TOP_CENTER, true, size, selectedColor, deselectedColor);
        }

        @Override
        public void receiveMouseDragged(MouseEvent event) {
            parent.setTranslateY(initTranslateY + event.getSceneY() - initMouseY);
            parent.setHeight(initHeight + initMouseY - event.getSceneY());
        }

        @Override
        public Bounds calculateBounds() {
            return new BoundingBox(parent.getWidth() / 2 - getSize() /2, 0, getSize(), getSize());
        }
    }

    /**
     * Top right resizer control point.
     */
    private static class TopRight extends PositionedControlPoint {

        TopRight(Visual parent, boolean moveConstrained, double size, Color selectedColor, Color deselectedColor) {
            super(parent, Position.TOP_RIGHT, moveConstrained, size, selectedColor, deselectedColor);
        }

        @Override
        public void receiveMouseDragged(MouseEvent event) {
            parent.setWidth(initWidth + event.getSceneX() - initMouseX);

            if (!isMoveConstrained()) {
                parent.setTranslateY(initTranslateY + event.getSceneY() - initMouseY);
                parent.setHeight(initHeight + initMouseY - event.getSceneY());
            } else {
                parent.setTranslateY(initTranslateY + initMouseX - event.getSceneX());
                parent.setHeight(initHeight + event.getSceneX() - initMouseX);
            }
        }

        @Override
        public Bounds calculateBounds() {
            return new BoundingBox(parent.getWidth()- getSize(), 0, getSize(), getSize());
        }
    }

    /**
     * Middle left resizer control point.
     */
    private static class MiddleLeft extends PositionedControlPoint {

        MiddleLeft(Visual parent, double size, Color selectedColor, Color deselectedColor) {
            super(parent, Position.MIDDLE_LEFT, true, size, selectedColor, deselectedColor);
        }

        @Override
        public void receiveMouseDragged(MouseEvent event) {
            parent.setTranslateX(initTranslateX + event.getSceneX() - initMouseX);
            parent.setWidth(initWidth + initMouseX - event.getSceneX());
        }

        @Override
        public Bounds calculateBounds() {
            return new BoundingBox(1, parent.getHeight() / 2 - getSize() /2, getSize(), getSize());
        }
    }

    /**
     * Middle right resizer control point.
     */
    private static class MiddleRight extends PositionedControlPoint {

        MiddleRight(Visual parent, double size, Color selectedColor, Color deselectedColor) {
            super(parent, Position.MIDDLE_RIGHT, true, size, selectedColor, deselectedColor);
        }

        @Override
        public void receiveMouseDragged(MouseEvent event) {
            parent.setWidth(initWidth + event.getSceneX() - initMouseX);
        }

        @Override
        public Bounds calculateBounds() {
            return new BoundingBox(parent.getWidth()- getSize(), parent.getHeight() / 2 - getSize() /2, getSize(), getSize());
        }
    }

    /**
     * Bottom left resizer control point.
     */
    private static class BottomLeft extends PositionedControlPoint {

        BottomLeft(Visual parent, boolean moveConstrained, double size, Color selectedColor, Color deselectedColor) {
            super(parent, Position.BOTTOM_LEFT, moveConstrained, size, selectedColor, deselectedColor);
        }

        @Override
        public void receiveMouseDragged(MouseEvent event) {
            parent.setTranslateX(initTranslateX + event.getSceneX() - initMouseX);
            parent.setWidth(initWidth + initMouseX - event.getSceneX());
            if (!isMoveConstrained()) {
                parent.setHeight(initHeight + event.getSceneY() - initMouseY);
            } else {
                parent.setHeight(initHeight + initMouseX - event.getSceneX());
            }
        }

        @Override
        public Bounds calculateBounds() {
            return new BoundingBox(0, parent.getHeight() - getSize(), getSize(), getSize());
        }
    }

    /**
     * Bottom center resizer control point.
     */
    private static class BottomCenter extends PositionedControlPoint {

        BottomCenter(Visual parent, double size, Color selectedColor, Color deselectedColor) {
            super(parent, Position.BOTTOM_CENTER, true, size, selectedColor, deselectedColor);
        }

        @Override
        public void receiveMouseDragged(MouseEvent event) {
            parent.setHeight(initHeight + event.getSceneY() - initMouseY);
        }

        @Override
        public Bounds calculateBounds() {
            return new BoundingBox(parent.getWidth() / 2 - getSize() /2, parent.getHeight() - getSize(), getSize(), getSize());
        }
    }

    /**
     * Bottom right resizer control point.
     */
    private static class BottomRight extends PositionedControlPoint {

        BottomRight(Visual parent, boolean moveConstrained, double size, Color selectedColor, Color deselectedColor) {
            super(parent, Position.BOTTOM_RIGHT, moveConstrained, size, selectedColor, deselectedColor);
        }

        @Override
        public void receiveMouseDragged(MouseEvent event) {
            parent.setWidth(initWidth + event.getSceneX() - initMouseX);
            if (!isMoveConstrained()) {
                parent.setHeight(initHeight + event.getSceneY() - initMouseY);
            } else {
                parent.setHeight(initHeight + event.getSceneX() - initMouseX);
            }
        }

        @Override
        public Bounds calculateBounds() {
            return new BoundingBox(parent.getWidth()- getSize(), parent.getHeight() - getSize(), getSize(), getSize());
        }
    }
}
