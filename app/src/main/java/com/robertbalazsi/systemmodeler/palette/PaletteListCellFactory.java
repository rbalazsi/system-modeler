package com.robertbalazsi.systemmodeler.palette;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

/**
 * * Defines a cell factory of the palette item ListView.
 */
public class PaletteListCellFactory implements Callback<TreeView<PaletteItem>, TreeCell<PaletteItem>> {
    @Override
    public TreeCell<PaletteItem> call(TreeView<PaletteItem> param) {
        return new PaletteItemCell();
    }
}
