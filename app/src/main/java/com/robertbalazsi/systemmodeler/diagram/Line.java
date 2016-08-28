package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.ControlPoint;

import java.util.Collection;

/**
 * A line on a diagram.
 */
public class Line extends Visual {

    public Line(double width, double height) {
        super(width, height);
    }

    @Override
    protected Collection<? extends ControlPoint> setupControlPoints() {
        return null;
    }

    @Override
    public Visual copy() {
        return null;
    }

    @Override
    protected void draw() {

    }
}
