package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.ControlFrame;
import com.robertbalazsi.systemmodeler.controlpoint.ControlFrameFactory;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * A rounded rectangle on a diagram.
 */
public class RoundedRectangle extends Visual {

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

    public RoundedRectangle(double width, double height, double arcWidth, double arcHeight) {
        super(width, height);
        setArcWidth(arcWidth);
        setArcHeight(arcHeight);
        draw();
        this.arcWidth.addListener(listener -> {redraw();});
        this.arcHeight.addListener(listener -> {redraw();});
    }

    @Override
    protected ControlFrame setupControlFrame() {
        return ControlFrameFactory.eightDirectionalRectangle(this);
    }

    @Override
    protected void draw() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.setFill(Color.BLUE);
        gc.setStroke(Color.TRANSPARENT);
        gc.fillRoundRect(getPadding(), getPadding(), getWidth() - 2*getPadding(), getHeight() - 2*getPadding(),
                getArcWidth(), getArcHeight());
        gc.applyEffect(new DropShadow(2.0, 3.0, 3.0, Color.GREY));
        gc.save();
    }

    @Override
    public Visual copy() {
        RoundedRectangle roundedRect = new RoundedRectangle(getWidth(), getHeight(), getArcWidth(), getArcHeight());
        baseCopy(this, roundedRect);
        return roundedRect;
    }
}
