package com.robertbalazsi.systemmodeler.palette;

import lombok.Getter;

/**
 * Defines a palette item having a name and a type (category or item).
 */
public class PaletteItem {

    @Getter
    private String name;

    @Getter
    private ItemType type;

    public PaletteItem(String name, ItemType type) {
        this.name = name;
        this.type = type;
    }

    enum ItemType {
        ITEM,
        CATEGORY
    }
}
