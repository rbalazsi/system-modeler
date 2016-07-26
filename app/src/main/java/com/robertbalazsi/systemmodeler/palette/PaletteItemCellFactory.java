package com.robertbalazsi.systemmodeler.palette;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Defines the cell factory for the Palette's item list.
 */
public class PaletteItemCellFactory implements Callback<ListView<PaletteItem>, ListCell<PaletteItem>> {

    @Override
    public ListCell<PaletteItem> call(ListView<PaletteItem> param) {
        return new PaletteItemCell();
    }
}
