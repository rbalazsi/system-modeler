package com.robertbalazsi.systemmodeler.global;

import com.robertbalazsi.systemmodeler.diagram.DiagramItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Global registry of diagram items. They are stored in a map and can be retrieved by ID.
 */
public class DiagramItemRegistry {
    private static final Map<String, DiagramItem> itemsMap = new HashMap<>();

    public static void putItem(DiagramItem item) {
        if (itemsMap.containsKey(item.getId())) {
            throw new IllegalArgumentException(String.format("Cannot register item with the same id '%s'", item.getId()));
        }
        itemsMap.put(item.getId(), item);
    }

    public static DiagramItem getItem(String id) {
        return itemsMap.get(id);
    }

    public static DiagramItem removeItem(String id) {
        return itemsMap.remove(id);
    }
}
