package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.ControlFrame;
import com.robertbalazsi.systemmodeler.controlpoint.ControlFrameFactory;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * A triangle on a diagram.
 */
public class Triangle extends Visual {

    public Triangle(double width, double height) {
        super(width, height);
        draw();
    }

    @Override
    protected ControlFrame setupControlFrame() {
        return ControlFrameFactory.eightDirectionalRectangle(this);
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
        gc.applyEffect(new DropShadow(2.0, 3.0, 3.0, Color.GREY));
        gc.save();
    }

    @Override
    public Visual copy() {
        Triangle triangle = new Triangle(getWidth(), getHeight());
        baseCopy(this, triangle);
        return triangle;
    }
}
