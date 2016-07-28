package com.robertbalazsi.systemmodeler.palette;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * * Defines a cell factory of the palette item ListView.
 */
public class PaletteListCellFactory implements Callback<ListView<String>, ListCell<String>> {
    @Override
    public ListCell<String> call(ListView<String> param) {
        return new PaletteItemCell();
    }
}
