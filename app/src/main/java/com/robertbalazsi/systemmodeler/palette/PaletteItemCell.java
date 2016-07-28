package com.robertbalazsi.systemmodeler.palette;

import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * Defines a cell in the palette item list.
 */
public class PaletteItemCell extends ListCell<String> {
    public PaletteItemCell() {
        this.setOnDragDetected(event -> {
            Dragboard dragboard = startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString(getItem());
            dragboard.setContent(content);

            event.consume();
        });
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
        } else {
            setText(item);
        }
    }
}
