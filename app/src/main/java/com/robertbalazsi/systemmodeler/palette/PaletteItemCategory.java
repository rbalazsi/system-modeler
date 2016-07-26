package com.robertbalazsi.systemmodeler.palette;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Defines an item category in the controls palette.
 */
public class PaletteItemCategory {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private List<PaletteItem> items = Lists.newArrayList();

    public PaletteItemCategory(String name) {
        this.name = name;
    }
}
