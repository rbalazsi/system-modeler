package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.ControlPoint;

import java.util.Collection;
import java.util.Collections;

/**
 * A label on a diagram.
 */
public class Label extends Visual {

    public Label(double width, double height) {
        super(width, height);
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

    @Override
    public Visual copy() {
        Label label = new Label(getWidth(), getHeight());
        baseCopy(this, label);
        return label;
    }
}
