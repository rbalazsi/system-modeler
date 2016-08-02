package com.robertbalazsi.systemmodeler.global;

import com.robertbalazsi.systemmodeler.diagram.CanvasItem;
import com.robertbalazsi.systemmodeler.diagram.Circle;
import com.robertbalazsi.systemmodeler.diagram.Ellipse;
import com.robertbalazsi.systemmodeler.diagram.Rectangle;

import java.util.Map;

/**
 * Defines the available palette items, their names to their corresponding shapes.
 */
public class PaletteItemRegistry {

    public static final String RECTANGLE = "Rectangle";
    public static final String CIRCLE = "Circle";
    public static final String ELLIPSE = "Ellipse";

    public static CanvasItem getItem(String name) {
        //TODO: make it polymorphic
        if (RECTANGLE.equals(name)) {
            return new Rectangle(200.0, 100.0);
        } else if (CIRCLE.equals(name)) {
            return new Circle(100.0);
        } else if (ELLIPSE.equals(name)) {
            return new Ellipse(200.0, 100.0);
        }
        throw new IllegalArgumentException(String.format("Shape '%s' not found in the registry.", name));
    }
}
