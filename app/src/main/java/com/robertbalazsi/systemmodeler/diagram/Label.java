package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.ControlPoint;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.Collection;
import java.util.Collections;

/**
 * A text on a diagram.
 */
public class Label extends DiagramItem {

    public Label(String id, double width, double height) {
        super(id, width, height);
        draw();
    }

    @Override
    protected Collection<? extends ControlPoint> setupControlPoints() {
        return Collections.emptyList();
    }

    @Override
    protected void draw() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(new Font(28));
        gc.setFill(Color.RED);
        gc.fillText("Text", 0, getHeight() / 2);
        gc.save();
    }
}
