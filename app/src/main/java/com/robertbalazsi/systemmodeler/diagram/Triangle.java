package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.ControlPoint;
import com.robertbalazsi.systemmodeler.controlpoint.Location;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Collection;

/**
 * A triangle on a diagram.
 */
public class Triangle extends DiagramItem {

    public Triangle(String id, double width, double height) {
        super(id, width, height);
        draw();
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
        gc.beginPath();
        gc.moveTo(getWidth() / 2, getPadding());
        gc.lineTo(getWidth() - getPadding(), getHeight() - getPadding());
        gc.lineTo(getPadding(), getHeight() - getPadding());
        gc.lineTo(getWidth() / 2, getPadding());
        gc.closePath();
        gc.fill();
        gc.save();
    }
}
