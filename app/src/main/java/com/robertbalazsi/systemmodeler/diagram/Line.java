package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.ControlFrame;
import com.robertbalazsi.systemmodeler.controlpoint.ControlPoint;

import java.util.Collection;

/**
 * A line on a diagram.
 */
public class Line extends Visual {

    //TODO: revise properties, hide width and height
    public Line(double length, double width) {
        super(length, width);
    }

    @Override
    protected ControlFrame setupControlFrame() {
        return null;
    }

    @Override
    public Visual copy() {
        Line line= new Line(getWidth(), getHeight());
        baseCopy(this, line);
        return line;
    }

    @Override
    protected void draw() {

    }
}
