package com.robertbalazsi.systemmodeler.global;

import com.robertbalazsi.systemmodeler.diagram.Visual;

import java.util.HashMap;
import java.util.Map;

/**
 * Global registry of visuals. IDs are automatically generated if needed. Visuals are stored in a map and can be retrieved by ID.
 */
public class VisualRegistry {
    private static final Map<String, Visual> itemsMap = new HashMap<>();
    private static long idCtr = 0;

    public static void put(Visual item) {
        String id = item.getId();
        if (itemsMap.containsKey(id)) {
            throw new IllegalArgumentException(String.format("Item with ID '%s' already exists.", id));
        }
        if (id == null) {
            item.setId("item_" + idCtr++);
        }
        itemsMap.put(item.getId(), item);
    }

    public static Visual get(String id) {
        return itemsMap.get(id);
    }

    public static Visual remove(String id) {
        return itemsMap.remove(id);
    }
}
