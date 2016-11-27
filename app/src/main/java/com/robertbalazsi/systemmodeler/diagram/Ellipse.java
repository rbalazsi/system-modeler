package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.ControlFrame;
import com.robertbalazsi.systemmodeler.controlpoint.ControlFrameFactory;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * An ellipse on a diagram.
 */
public class Ellipse extends Visual {

    public Ellipse(double width, double height) {
        super(width, height);
        draw();
    }

    @Override
    public ControlFrame setupControlFrame() {
        return ControlFrameFactory.eightDirectionalRectangle(this);
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
    public Visual copy() {
        Ellipse ellipse = new Ellipse(getWidth(), getHeight());
        baseCopy(this, ellipse);
        return ellipse;
    }
}
