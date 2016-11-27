package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.ControlFrame;
import com.robertbalazsi.systemmodeler.controlpoint.ControlFrameFactory;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * A rectangle on a diagram. 
 */
public class Rectangle extends Visual {

    public Rectangle(double width, double height) {
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
        gc.fillRect(getPadding(), getPadding(), getWidth() - 2*getPadding(), getHeight() - 2*getPadding());
        gc.applyEffect(new DropShadow(2.0, 3.0, 3.0, Color.GREY));
        gc.save();
    }

    @Override
    public Visual copy() {
        Rectangle rect = new Rectangle(getWidth(), getHeight());
        baseCopy(this, rect);
        return rect;
    }
}
