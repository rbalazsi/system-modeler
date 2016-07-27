package com.robertbalazsi.systemmodeler.palette;

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

    private static Map<String, Shape> itemsMap = Maps.newHashMap();

    // Builtin items
    static {
        itemsMap.put(RECTANGLE, new Rectangle(80.0, 30.0));
        itemsMap.put(CIRCLE, new Circle(15.0));
    }

    public static Shape getItem(String name) {
        return itemsMap.get(name);
    }

    public static void putItem(String name, Shape shape) {
        itemsMap.put(name, shape);
    }
}
