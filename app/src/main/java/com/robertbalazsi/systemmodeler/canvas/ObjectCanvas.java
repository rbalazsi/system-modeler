package com.robertbalazsi.systemmodeler.canvas;

import com.google.common.collect.Sets;
import com.robertbalazsi.systemmodeler.global.PaletteItemRegistry;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The canvas containing the objects of the system.
 */
public class ObjectCanvas extends Pane {

    //TODO observable collection
    private Set<CanvasItem> selection = Sets.newHashSet();

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

                    //TODO: decide on the domain object (use explicit objects for components and sub-systems)
                    CanvasItem canvasItem = new CanvasItem(paletteItem, null);
                    paletteItem.setOnMouseClicked(clickedEvent -> {
                        if( !clickedEvent.isShiftDown() && !clickedEvent.isControlDown()) {
                            // Remove all bounded boxes from the canvas
                            getChildren().removeAll(selection.stream().map(CanvasItem::getBoundedBox).collect(Collectors.toSet()));
                            selection.clear();
                        }

                        if (clickedEvent.isControlDown() && selection.contains(canvasItem)) {
                            selection.remove(canvasItem);
                            this.getChildren().remove(canvasItem.getBoundedBox());
                        } else {
                            if (!selection.contains(canvasItem)) {
                                selection.add(canvasItem);
                                this.getChildren().add(canvasItem.getBoundedBox());
                            }
                            //TODO: property page update
                        }
                    });

                    getChildren().add(paletteItem);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    //TODO: implement rubber band selection
    // http://stackoverflow.com/questions/28562509/how-to-select-multiple-components-of-canvas-using-ctrl-key-in-javafx
}
