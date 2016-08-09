package com.robertbalazsi.systemmodeler.global;

import com.robertbalazsi.systemmodeler.diagram.DiagramItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Global registry of diagram items. IDs are generated for each item. Items are stored in a map and can be retrieved by ID.
 */
public class DiagramItemRegistry {
    private static final Map<String, DiagramItem> itemsMap = new HashMap<>();
    private static long idCtr = 0;

    public static void putItem(DiagramItem item) {
        if (item.getId() != null) {
            throw new IllegalArgumentException("IDs for items are automatically generated. They cannot be given explicitly.");
        }
        item.setId("item_" + idCtr++);
        itemsMap.put(item.getId(), item);
    }

    public static DiagramItem getItem(String id) {
        return itemsMap.get(id);
    }

    public static DiagramItem removeItem(String id) {
        return itemsMap.remove(id);
    }
}
