package com.robertbalazsi.systemmodeler.global;

import com.google.common.collect.Maps;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.Map;

/**
 * Defines the available palette items, their names to their corresponding shapes.
 */
public class PaletteItemRegistry {

    public static final String RECTANGLE = "Rectangle";
    public static final String CIRCLE = "Circle";

    public static Shape getItem(String name) {
        //TODO: make it polymorphic
        if (RECTANGLE.equals(name)) {
            return new Rectangle(150.0, 70.0);
        } else if (CIRCLE.equals(name)) {
            return new Circle(60.0);
        }
        throw new IllegalArgumentException(String.format("Shape '%s' not found in the registry.", name));
    }
}
