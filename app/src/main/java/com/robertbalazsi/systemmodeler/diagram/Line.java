package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A line on a diagram.
 */
public class Line extends Visual {

    private DoubleProperty strokeSize = new SimpleDoubleProperty(this, "strokeSize");

    public final DoubleProperty strokeSizeProperty() {
        return strokeSize;
    }

    public final double getStrokeSize() {
        return strokeSize.get();
    }

    public final void setStrokeSize(double strokeSize) {
        this.strokeSize.set(strokeSize);
    }

    //TODO: !!! revise properties, hide width and height
    public Line(double length, double strokeSize) {
        super(length, strokeSize > ControlPoint.DEFAULT_SIZE ? strokeSize : ControlPoint.DEFAULT_SIZE);
        setStrokeSize(strokeSize);
        draw();
        this.strokeSize.addListener(listener -> redraw());
    }

    @Override
    protected ControlFrame setupControlFrame() {
        double initialY = getHeight() / 2 - ControlPoint.DEFAULT_SIZE /2;
        ControlPointGroup pointGroup = new ControlPointGroup(Arrays.asList(
                new GroupedControlPoint.Builder(this, 0, initialY).build(),
                new GroupedControlPoint.Builder(this, getWidth() - ControlPoint.DEFAULT_SIZE, initialY).build()
        ));
        return ControlFrameFactory.polygonalFrame(this, pointGroup);
    }

    @Override
    public Visual copy() {
        Line line = new Line(getWidth(), getStrokeSize());
        baseCopy(this, line);
        return line;
    }

    @Override
    protected void draw() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.setStroke(Color.BLUE);
        gc.setLineDashes(0);
        gc.setLineWidth(getStrokeSize());

        //TODO: make it general for more than 2 points
        GroupedControlPoint pointA = (GroupedControlPoint)controlPoints.get(0);
        GroupedControlPoint pointB = (GroupedControlPoint)controlPoints.get(1);
        gc.strokeLine(pointA.getX(), pointA.getY() + getStrokeSize() / 2, pointB.getX(), pointB.getY() + getStrokeSize() / 2);

        gc.applyEffect(new DropShadow(2.0, 3.0, 3.0, Color.GREY));
        gc.save();
    }
}
