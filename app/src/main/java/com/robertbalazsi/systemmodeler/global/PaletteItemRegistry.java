package com.robertbalazsi.systemmodeler.global;

import com.robertbalazsi.systemmodeler.diagram.DiagramItem;
import com.robertbalazsi.systemmodeler.diagram.Circle;
import com.robertbalazsi.systemmodeler.diagram.Ellipse;
import com.robertbalazsi.systemmodeler.diagram.Rectangle;
import com.robertbalazsi.systemmodeler.diagram.RoundedRectangle;

/**
 * Defines the available palette items, their names to their corresponding shapes.
 */
public class PaletteItemRegistry {

    public static final String RECTANGLE = "Rectangle";
    public static final String CIRCLE = "Circle";
    public static final String ELLIPSE = "Ellipse";
    public static final String ROUNDED_RECTANGLE = "Rounded Rectangle";

    public static long itemCtr = 0;

    public static DiagramItem getItem(String name) {
        //TODO: make it polymorphic
        String nextId = "item_" + (++itemCtr);
        if (RECTANGLE.equals(name)) {
            return new Rectangle(nextId, 200, 100);
        } else if (CIRCLE.equals(name)) {
            return new Circle(nextId, 100);
        } else if (ELLIPSE.equals(name)) {
            return new Ellipse(nextId, 200, 100);
        } else if (ROUNDED_RECTANGLE.equals(name)) {
            return new RoundedRectangle(nextId, 200, 100, 10, 10);
        }
        throw new IllegalArgumentException(String.format("Shape '%s' not found in the registry.", name));
    }
}
