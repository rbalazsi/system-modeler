package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.ControlFrame;
import com.robertbalazsi.systemmodeler.controlpoint.ControlFrameFactory;

/**
 * A label on a diagram.
 */
public class Label extends Visual {

    public Label(double width, double height) {
        super(width, height);
        draw();
    }

    @Override
    protected ControlFrame setupControlFrame() {
        return ControlFrameFactory.emptyFrame(this);
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
