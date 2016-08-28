package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.ControlPoint;
import com.robertbalazsi.systemmodeler.controlpoint.Location;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Collection;

/**
 * A circle on a diagram.
 */
public class Circle extends Visual {

    private DoubleProperty diameter = new SimpleDoubleProperty(this, "diameter");

    public final DoubleProperty diameterProperty() {
        return diameter;
    }

    public final double getDiameter() {
        return diameter.get();
    }

    public final void setDiameter(double diameter) {
        this.diameter.set(diameter);
    }

    public Circle(double diameter) {
        super(diameter, diameter);
        setDiameter(diameter);
        draw();
        this.diameter.addListener(listener -> {redraw();});
    }

    @Override
    protected Collection<? extends ControlPoint> setupControlPoints() {
        return Arrays.asList(
                new ControlPoint.Builder(this, Location.TOP_LEFT).moveConstrained().build(),
                new ControlPoint.Builder(this, Location.TOP_RIGHT).moveConstrained().build(),

                new ControlPoint.Builder(this, Location.BOTTOM_LEFT).moveConstrained().build(),
                new ControlPoint.Builder(this, Location.BOTTOM_RIGHT).moveConstrained().build()
        );
    }

    @Override
    public Visual copy() {
        Circle circle = new Circle(getDiameter());
        baseCopy(this, circle);
        return circle;
    }

    @Override
    protected void draw() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.setFill(Color.BLUE);
        gc.setStroke(Color.TRANSPARENT);
        gc.fillOval(getPadding(), getPadding(), getWidth() - 2*getPadding(), getHeight() - 2*getPadding());
        gc.applyEffect(new DropShadow(2.0, 3.0, 3.0, Color.GREY));
        gc.save();
    }

    @Override
    public boolean alwaysMaintainsAspectRatio() {
        return true;
    }
}
