package com.robertbalazsi.systemmodeler.controlpoint;

import com.robertbalazsi.systemmodeler.diagram.CanvasItem;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * A control point on a diagram item used for resizing it.
 * //TODO: update doc when rotation is implemented
 */
public abstract class ControlPoint {

    public static final double DEFAULT_SIZE = 7;
    public static final Color DEFAULT_DESELECTED_COLOR = Color.BLACK;
    public static final Color DEFAULT_SELECTED_COLOR = Color.RED;

    //TODO: review and define some of these as observable properties
    protected CanvasItem parent;
    protected Location location;
    protected boolean moveConstrained;
    protected double size;
    protected Color selectedColor;
    protected Color deselectedColor;
    protected Bounds bounds;

    protected double initMouseX, initMouseY;
    protected double initTranslateX, initTranslateY;
    protected double initWidth, initHeight;

    protected ControlPoint(CanvasItem parent, Location location, boolean moveConstrained, double size, Color selectedColor,
                           Color deselectedColor) {
        this.parent = parent;
        this.location = location;
        this.moveConstrained = moveConstrained;
        this.size = size != 0 ? size : DEFAULT_SIZE;
        this.deselectedColor = deselectedColor != null ? deselectedColor : DEFAULT_DESELECTED_COLOR;
        this.selectedColor = selectedColor != null ? selectedColor : DEFAULT_SELECTED_COLOR;
        refreshBounds();
    }

    public Location getLocation() {
        return location;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void select() {
        draw(selectedColor);
    }

    public void deselect() {
        draw(deselectedColor);
    }

    public void clear() {
        GraphicsContext gc = parent.getGraphicsContext2D();
        gc.clearRect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
        gc.save();
    }

    public void refreshBounds() {
        bounds = calculateBounds();
    }

    public void receiveMousePressed(MouseEvent event) {
        initMouseX = event.getSceneX();
        initMouseY = event.getSceneY();
        initTranslateX = parent.getTranslateX();
        initTranslateY = parent.getTranslateY();
        initWidth = parent.getWidth();
        initHeight = parent.getHeight();
    }

    public abstract void receiveMouseDragged(MouseEvent event);

    public abstract Bounds calculateBounds();

    private void draw(Color color) {
        GraphicsContext gc = parent.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
        gc.save();
    }

    /**
     * Builder class for {@link ControlPoint}.
     */
    public static class Builder {

        private final CanvasItem parent;
        private final Location location;
        private boolean moveConstrained;
        private double size;
        private Color deselectedColor;
        private Color selectedColor;

        public Builder(CanvasItem parent, Location location) {
            this.parent = parent;
            this.location = location;
        }

        public Builder size(double size) {
            this.size = size;
            return this;
        }

        public Builder moveConstrained() {
            this.moveConstrained = true;
            return this;
        }

        public Builder deselectedColor(Color color) {
            this.deselectedColor = color;
            return this;
        }

        public Builder selectedColor(Color color) {
            this.selectedColor = color;
            return this;
        }

        public ControlPoint build() {
            return Factory.create(this);
        }
    }

    /**
     * Hidden class for deciding the control point type with respect to its location.
     */
    private static class Factory {
        static ControlPoint create(Builder builder) {
            switch (builder.location) {
                case TOP_LEFT:
                    return new TopLeft(builder.parent, builder.moveConstrained, builder.size, builder.selectedColor, builder.deselectedColor);
                case TOP_CENTER:
                    return new TopCenter(builder.parent, builder.size, builder.selectedColor, builder.deselectedColor);
                case TOP_RIGHT:
                    return new TopRight(builder.parent, builder.moveConstrained, builder.size, builder.selectedColor, builder.deselectedColor);
                case MIDDLE_LEFT:
                    return new MiddleLeft(builder.parent, builder.moveConstrained, builder.size, builder.selectedColor, builder.deselectedColor);
                case MIDDLE_RIGHT:
                    return new MiddleRight(builder.parent, builder.moveConstrained, builder.size, builder.selectedColor, builder.deselectedColor);
                case BOTTOM_LEFT:
                    return new BottomLeft(builder.parent, builder.moveConstrained, builder.size, builder.selectedColor, builder.deselectedColor);
                case BOTTOM_CENTER:
                    return new BottomCenter(builder.parent, builder.moveConstrained, builder.size, builder.selectedColor, builder.deselectedColor);
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
    private static class TopLeft extends ControlPoint {

        TopLeft(CanvasItem parent, boolean moveConstrained, double size, Color selectedColor, Color deselectedColor) {
            super(parent, Location.TOP_LEFT, moveConstrained, size, selectedColor, deselectedColor);
        }

        @Override
        public void receiveMouseDragged(MouseEvent event) {
            parent.setTranslateX(initTranslateX + event.getSceneX() - initMouseX);
            parent.setWidth(initWidth + initMouseX - event.getSceneX());

            if (!moveConstrained) {
                parent.setTranslateY(initTranslateY + event.getSceneY() - initMouseY);
                parent.setHeight(initHeight + initMouseY - event.getSceneY());
            } else {
                parent.setTranslateY(initTranslateX + event.getSceneX() - initMouseX);
                parent.setHeight(initHeight + initMouseX - event.getSceneX());
            }
        }

        @Override
        public Bounds calculateBounds() {
            return new BoundingBox(0, 0, size, size);
        }
    }

    /**
     * Top center resizer control point.
     */
    private static class TopCenter extends ControlPoint {

        TopCenter(CanvasItem parent, double size, Color selectedColor, Color deselectedColor) {
            super(parent, Location.TOP_CENTER, true, size, selectedColor, deselectedColor);
        }

        @Override
        public void receiveMouseDragged(MouseEvent event) {
            parent.setTranslateY(initTranslateY + event.getSceneY() - initMouseY);
            parent.setHeight(initHeight + initMouseY - event.getSceneY());
        }

        @Override
        public Bounds calculateBounds() {
            return new BoundingBox(parent.getWidth() / 2 - size /2, 0, size, size);
        }
    }

    /**
     * Top right resizer control point.
     */
    private static class TopRight extends ControlPoint {

        TopRight(CanvasItem parent, boolean moveConstrained, double size, Color selectedColor, Color deselectedColor) {
            super(parent, Location.TOP_RIGHT, moveConstrained, size, selectedColor, deselectedColor);
        }

        @Override
        public void receiveMouseDragged(MouseEvent event) {
            parent.setWidth(initWidth + event.getSceneX() - initMouseX);

            if (!moveConstrained) {
                parent.setTranslateY(initTranslateY + event.getSceneY() - initMouseY);
                parent.setHeight(initHeight + initMouseY - event.getSceneY());
            } else {
                parent.setTranslateY(initTranslateY + initMouseX - event.getSceneX());
                parent.setHeight(initHeight + event.getSceneX() - initMouseX);
            }
        }

        @Override
        public Bounds calculateBounds() {
            return new BoundingBox(parent.getWidth()- size, 0, size, size);
        }
    }

    /**
     * Middle left resizer control point.
     */
    private static class MiddleLeft extends ControlPoint {

        MiddleLeft(CanvasItem parent, boolean moveConstrained, double size, Color selectedColor, Color deselectedColor) {
            super(parent, Location.MIDDLE_LEFT, moveConstrained, size, selectedColor, deselectedColor);
        }

        @Override
        public void receiveMouseDragged(MouseEvent event) {
            parent.setTranslateX(initTranslateX + event.getSceneX() - initMouseX);
            parent.setWidth(initWidth + initMouseX - event.getSceneX());
        }

        @Override
        public Bounds calculateBounds() {
            return new BoundingBox(1, parent.getHeight() / 2 - size /2, size, size);
        }
    }

    /**
     * Middle right resizer control point.
     */
    private static class MiddleRight extends ControlPoint {

        MiddleRight(CanvasItem parent, boolean moveConstrained, double size, Color selectedColor, Color deselectedColor) {
            super(parent, Location.MIDDLE_RIGHT, moveConstrained, size, selectedColor, deselectedColor);
        }

        @Override
        public void receiveMouseDragged(MouseEvent event) {
            parent.setWidth(initWidth + event.getSceneX() - initMouseX);
        }

        @Override
        public Bounds calculateBounds() {
            return new BoundingBox(parent.getWidth()- size, parent.getHeight() / 2 - size /2, size, size);
        }
    }

    /**
     * Bottom left resizer control point.
     */
    private static class BottomLeft extends ControlPoint {

        BottomLeft(CanvasItem parent, boolean moveConstrained, double size, Color selectedColor, Color deselectedColor) {
            super(parent, Location.BOTTOM_LEFT, moveConstrained, size, selectedColor, deselectedColor);
        }

        @Override
        public void receiveMouseDragged(MouseEvent event) {
            parent.setTranslateX(initTranslateX + event.getSceneX() - initMouseX);
            parent.setWidth(initWidth + initMouseX - event.getSceneX());
            if (!moveConstrained) {
                parent.setHeight(initHeight + event.getSceneY() - initMouseY);
            } else {
                parent.setHeight(initHeight + event.getSceneX() - initMouseX);
            }
        }

        @Override
        public Bounds calculateBounds() {
            return new BoundingBox(0, parent.getHeight()- size, size, size);
        }
    }

    /**
     * Bottom center resizer control point.
     */
    private static class BottomCenter extends ControlPoint {

        BottomCenter(CanvasItem parent, boolean moveConstrained, double size, Color selectedColor, Color deselectedColor) {
            super(parent, Location.BOTTOM_CENTER, moveConstrained, size, selectedColor, deselectedColor);
        }

        @Override
        public void receiveMouseDragged(MouseEvent event) {
            parent.setHeight(initHeight + event.getSceneY() - initMouseY);
        }

        @Override
        public Bounds calculateBounds() {
            return new BoundingBox(parent.getWidth() / 2 - size /2, parent.getHeight()- size, size, size);
        }
    }

    /**
     * Bottom right resizer control point.
     */
    private static class BottomRight extends ControlPoint {

        BottomRight(CanvasItem parent, boolean moveConstrained, double size, Color selectedColor, Color deselectedColor) {
            super(parent, Location.BOTTOM_RIGHT, moveConstrained, size, selectedColor, deselectedColor);
        }

        @Override
        public void receiveMouseDragged(MouseEvent event) {
            parent.setWidth(initWidth + event.getSceneX() - initMouseX);
            if (!moveConstrained) {
                parent.setHeight(initHeight + event.getSceneY() - initMouseY);
            } else {
                parent.setHeight(initHeight + event.getSceneX() - initMouseX);
            }
        }

        @Override
        public Bounds calculateBounds() {
            return new BoundingBox(parent.getWidth()- size, parent.getHeight()- size, size, size);
        }
    }
}
