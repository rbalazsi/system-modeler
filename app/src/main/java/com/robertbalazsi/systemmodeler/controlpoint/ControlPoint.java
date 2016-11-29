package com.robertbalazsi.systemmodeler.controlpoint;

import com.robertbalazsi.systemmodeler.diagram.Visual;
import javafx.beans.property.*;
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

    private BooleanProperty moveConstrained = new SimpleBooleanProperty(this, "moveConstrained", false);

    public final BooleanProperty moveConstrainedProperty() {
        return moveConstrained;
    }

    public final boolean isMoveConstrained() {
        return moveConstrained.get();
    }

    public final void setMoveConstrained(boolean constrained) {
        moveConstrained.set(constrained);
    }

    private DoubleProperty size = new SimpleDoubleProperty(this, "size", DEFAULT_SIZE);

    public final DoubleProperty sizeProperty() {
        return size;
    }

    public final double getSize() {
        return size.get();
    }

    public final void setSize(double size) {
        this.size.set(size);
    }

    private ObjectProperty<Color> selectedColor = new SimpleObjectProperty<>(this, "selectedColor", DEFAULT_SELECTED_COLOR);

    public final ObjectProperty<Color> selectedColorProperty() {
        return selectedColor;
    }

    public final Color getSelectedColor() {
        return selectedColor.get();
    }

    public final void setSelectedColor(Color color) {
        selectedColor.set(color);
    }

    private ObjectProperty<Color> deselectedColor = new SimpleObjectProperty<>(this, "deselectedColor", DEFAULT_DESELECTED_COLOR);

    public final ObjectProperty<Color> deselectedColorProperty() {
        return deselectedColor;
    }

    public final Color getDeselectedColor() {
        return deselectedColor.get();
    }

    public final void setDeselectedColor(Color color) {
        deselectedColor.set(color);
    }

    protected Visual parent;
    protected Bounds bounds;

    protected double initMouseX, initMouseY;
    protected double initTranslateX, initTranslateY;
    protected double initWidth, initHeight;

    protected ControlPoint(Visual parent, boolean moveConstrained, double size, Color selectedColor,
                           Color deselectedColor) {
        this.parent = parent;
        setMoveConstrained(moveConstrained);
        if (size != 0) {
            setSize(size);
        }
        if (deselectedColor != null) {
            setDeselectedColor(deselectedColor);
        }
        if (selectedColor != null) {
            setSelectedColor(selectedColor);
        }
        refreshBounds();
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void select() {
        draw(getSelectedColor());
    }

    public void deselect() {
        draw(getDeselectedColor());
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
     * Base builder class for {@link ControlPoint}.
     */
    public static abstract class Builder<B extends Builder, E extends ControlPoint> {

        protected final Visual parent;

        //TODO: !! provide means to define flexible constraints (X axis, Y axis, diagonal - same ammount in both directions, other?)
        protected boolean moveConstrained;
        protected double size;
        protected Color deselectedColor;
        protected Color selectedColor;

        public Builder(Visual parent) {
            this.parent = parent;
        }

        public B size(double size) {
            this.size = size;
            return getThis();
        }

        public B moveConstrained() {
            this.moveConstrained = true;
            return getThis();
        }

        public B deselectedColor(Color color) {
            this.deselectedColor = color;
            return getThis();
        }

        public B selectedColor(Color color) {
            this.selectedColor = color;
            return getThis();
        }

        abstract B getThis();

        public abstract E build();
    }
}
