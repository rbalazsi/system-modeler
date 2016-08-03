package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.ControlPoint;
import com.robertbalazsi.systemmodeler.controlpoint.Location;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Collection;

/**
 * A circle on a diagram.
 */
public class Circle extends DiagramItem {

    public Circle(String id, double diameter) {
        super(id, diameter, diameter);
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
    protected void drawItem() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.setFill(Color.BLUE);
        gc.setStroke(Color.TRANSPARENT);
        //TODO: extract border size as property?
        gc.fillOval(3, 3, getWidth() - 6, getHeight() - 6);
        gc.save();
    }
}
