package com.robertbalazsi.systemmodeler.command;

import com.robertbalazsi.systemmodeler.diagram.DiagramItem;
import javafx.geometry.Bounds;

/**
 * Encapsulates the undoable resizing of diagram items.
 */
public class ResizeItemCommand implements Command {

    private final DiagramItem item;
    private final Bounds originalBounds;
    private final Bounds newBounds;

    public ResizeItemCommand(DiagramItem item, Bounds originalBounds, Bounds newBounds) {
        this.item = item;
        this.originalBounds = originalBounds;
        this.newBounds = newBounds;
    }

    @Override
    public void execute() {
        item.setLayoutX(newBounds.getMinX());
        item.setLayoutY(newBounds.getMinY());
        item.setWidth(newBounds.getWidth());
        item.setHeight(newBounds.getHeight());
    }

    @Override
    public void undo() {
        item.setLayoutX(originalBounds.getMinX());
        item.setLayoutY(originalBounds.getMinY());
        item.setWidth(originalBounds.getWidth());
        item.setHeight(originalBounds.getHeight());
    }
}
