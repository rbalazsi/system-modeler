package com.robertbalazsi.systemmodeler.palette;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.ImageCursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 * Defines a ListView cell representation of a PaletteItem..
 */
public class PaletteItemCell extends ListCell<PaletteItem> {
    @Override
    protected void updateItem(PaletteItem item, boolean empty) {
        if (item != null) {
            //TODO: use resource cache
            this.setCursor(new ImageCursor(new Image("cursors/4headed_arrow.png")));
            this.setGraphic(new Label(item.getName()));
            
            //TODO: externalize in css
            this.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                }
            });
            this.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                }
            });
        }
    }
}
