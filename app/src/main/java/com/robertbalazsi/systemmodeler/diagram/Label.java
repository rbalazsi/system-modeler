package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.ControlPoint;

import java.util.Collection;
import java.util.Collections;

/**
 * A label on a diagram.
 */
public class Label extends DiagramItem {

    public Label(String id, double width, double height) {
        super(id, width, height);
        draw();
    }

    @Override
    protected Collection<? extends ControlPoint> setupControlPoints() {
        return Collections.EMPTY_LIST;
    }

    @Override
    protected void draw() {
        /* empty */
    }
}
