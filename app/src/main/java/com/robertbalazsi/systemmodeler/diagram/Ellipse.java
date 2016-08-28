package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.ControlPoint;
import com.robertbalazsi.systemmodeler.controlpoint.Location;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Collection;

/**
 * An ellipse on a diagram.
 */
public class Ellipse extends Visual {

    public Ellipse(double width, double height) {
        super(width, height);
        draw();
    }

    @Override
    public Collection<? extends ControlPoint> setupControlPoints() {
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
