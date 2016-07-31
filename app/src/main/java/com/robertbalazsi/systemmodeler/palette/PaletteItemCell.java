package com.robertbalazsi.systemmodeler.palette;

import javafx.scene.control.TreeCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * Defines a cell in the palette item list.
 */
public class PaletteItemCell extends TreeCell<PaletteItem> {

    @Override
    protected void updateItem(PaletteItem item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
        } else {
            setText(item.getName());
        }

        if (item != null && item.getType() == PaletteItem.ItemType.ITEM) {
            this.setOnDragDetected(event -> {
                Dragboard dragboard = startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.putString(getItem().getName());
                dragboard.setContent(content);

                event.consume();
            });
        }
    }
}
