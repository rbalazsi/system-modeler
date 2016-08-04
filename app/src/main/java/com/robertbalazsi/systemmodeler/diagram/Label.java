package com.robertbalazsi.systemmodeler.diagram;

import com.robertbalazsi.systemmodeler.controlpoint.ControlPoint;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.Collection;
import java.util.Collections;

/**
 * The label property of a diagram item.
 * TODO: continue implementing
 */
public class Label {

    private final DiagramItem parent;

    private StringProperty text = new SimpleStringProperty(this, "text");

    public final StringProperty textProperty() {
        return text;
    }

    public final String getText(DiagramItem parent) {
        return text.get();
    }

    public final void setText(String text) {
        this.text.set(text);
    }

    public Label(DiagramItem parent) {
        this.parent = parent;
    }

    public Label(DiagramItem parent, String text) {
        this(parent);
        setText(text);
    }

    /*GraphicsContext gc = this.getGraphicsContext2D();
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(new Font(28));
        gc.setFill(Color.RED);
        gc.fillText("Text", 0, getHeight() / 2);
        gc.save();*/
}
