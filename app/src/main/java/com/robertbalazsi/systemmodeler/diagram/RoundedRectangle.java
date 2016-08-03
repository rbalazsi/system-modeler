package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.ControlPoint;
import com.robertbalazsi.systemmodeler.controlpoint.Location;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Collection;

/**
 * A rounded rectangle on a diagram.
 */
public class RoundedRectangle extends DiagramItem {

    private DoubleProperty arcWidth = new SimpleDoubleProperty(this, "arcWidth");

    public final DoubleProperty arcWidthProperty() {
        return arcWidth;
    }

    public final double getArcWidth() {
        return arcWidth.get();
    }

    public final void setArcWidth(double arcWidth) {
        this.arcWidth.set(arcWidth);
    }

    private DoubleProperty arcHeight = new SimpleDoubleProperty(this, "arcHeight");

    public final DoubleProperty arcHeightProperty() {
        return arcHeight;
    }

    public final double getArcHeight() {
        return arcHeight.get();
    }

    public final void setArcHeight(double arcHeight) {
        this.arcHeight.set(arcHeight);
    }

    public RoundedRectangle(String id, double width, double height, double arcWidth, double arcHeight) {
        super(id, width, height);
        setArcWidth(arcWidth);
        setArcHeight(arcHeight);
        draw();
        this.arcWidth.addListener(listener -> {redraw();});
        this.arcHeight.addListener(listener -> {redraw();});
    }

    @Override
    protected Collection<? extends ControlPoint> setupControlPoints() {
        return Arrays.asList(
                new ControlPoint.Builder(this, Location.TOP_LEFT).build(),
                new ControlPoint.Builder(this, Location.TOP_CENTER).build(),
                new ControlPoint.Builder(this, Location.TOP_RIGHT).build(),

                new ControlPoint.Builder(this, Location.MIDDLE_LEFT).build(),
                new ControlPoint.Builder(this, Location.MIDDLE_RIGHT).build(),

                new ControlPoint.Builder(this, Location.BOTTOM_LEFT).build(),
                new ControlPoint.Builder(this, Location.BOTTOM_CENTER).build(),
                new ControlPoint.Builder(this, Location.BOTTOM_RIGHT).build()
        );
    }

    @Override
    protected void draw() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.setFill(Color.BLUE);
        gc.setStroke(Color.TRANSPARENT);
        gc.fillRoundRect(getPadding(), getPadding(), getWidth() - 2*getPadding(), getHeight() - 2*getPadding(),
                getArcWidth(), getArcHeight());
        gc.save();
    }
}
