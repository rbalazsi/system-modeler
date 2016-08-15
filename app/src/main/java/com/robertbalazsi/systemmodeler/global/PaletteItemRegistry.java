package com.robertbalazsi.systemmodeler.global;

import com.robertbalazsi.systemmodeler.diagram.*;

/**
 * Defines the available palette items, their names to their corresponding shapes.
 */
public class PaletteItemRegistry {

    public static final String RECTANGLE = "Rectangle";
    public static final String CIRCLE = "Circle";
    public static final String ELLIPSE = "Ellipse";
    public static final String ROUNDED_RECTANGLE = "Rounded rectangle";
    public static final String TRIANGLE = "Triangle";
    public static final String LABEL = "Label";

    public static DiagramItem getItem(String name) {
        //TODO: make it polymorphic
        if (RECTANGLE.equals(name)) {
            return new Rectangle(200, 100);
        } else if (CIRCLE.equals(name)) {
            return new Circle(100);
        } else if (ELLIPSE.equals(name)) {
            return new Ellipse(200, 100);
        } else if (ROUNDED_RECTANGLE.equals(name)) {
            return new RoundedRectangle(200, 100, 15, 15);
        } else if (TRIANGLE.equals(name)) {
            return new Triangle(200, 100);
        } else if (LABEL.equals(name)) {
            Label label = new Label(150, 80);
            label.setText("Label");
            return label;
        }
        throw new IllegalArgumentException(String.format("Shape '%s' not found in the registry.", name));
    }
}
