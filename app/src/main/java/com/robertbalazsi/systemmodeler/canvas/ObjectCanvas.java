package com.robertbalazsi.systemmodeler.canvas;

import com.robertbalazsi.systemmodeler.palette.PaletteItemRegistry;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

import java.io.IOException;

/**
 * The canvas containing the objects of the system.
 */
public class ObjectCanvas extends Pane {

    public ObjectCanvas() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/objectCanvas.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setupDragDropHandlers();
    }

    private void setupDragDropHandlers() {
        this.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != this) {
                    //TODO: review the transfer modes
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });

        //TODO: provide visual feedback by implementing onDragEntered() and onDragExited()

        this.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasString()) {
                Shape paletteItem = PaletteItemRegistry.getItem(dragboard.getString());
                if (paletteItem != null) {
                    paletteItem.relocate(event.getX(), event.getY());
                    getChildren().add(paletteItem);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }
}
