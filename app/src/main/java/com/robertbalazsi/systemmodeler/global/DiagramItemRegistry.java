package com.robertbalazsi.systemmodeler.global;

import com.robertbalazsi.systemmodeler.diagram.DiagramItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Global registry of diagram items. IDs are automatically generated if needed. Items are stored in a map and can be retrieved by ID.
 */
public class DiagramItemRegistry {
    private static final Map<String, DiagramItem> itemsMap = new HashMap<>();
    private static long idCtr = 0;

    public static void putItem(DiagramItem item) {
        String id = item.getId();
        if (itemsMap.containsKey(id)) {
            throw new IllegalArgumentException(String.format("Item with ID '%s' already exists."));
        }
        if (id == null) {
            item.setId("item_" + idCtr++);
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
