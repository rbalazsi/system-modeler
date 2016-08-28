package com.robertbalazsi.systemmodeler.command;

import com.robertbalazsi.systemmodeler.diagram.Diagram;
import com.robertbalazsi.systemmodeler.diagram.Visual;
import com.robertbalazsi.systemmodeler.global.VisualRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the undoable operation of deleting visuals from a diagram.
 */
public class DeleteItemsCommand implements Command {

    private final Diagram diagram;
    private final List<Visual> itemsToDelete;
    private List<Visual> previousItems;

    public DeleteItemsCommand(Diagram diagram, List<Visual> itemsToDelete) {
        this.diagram = diagram;
        this.itemsToDelete = itemsToDelete;
        previousItems = new ArrayList<>(itemsToDelete);
    }

    @Override
    public void execute() {
        for (Visual item : itemsToDelete) {
            VisualRegistry.remove(item.getId());
            diagram.getChildren().remove(item);
        }
    }

    @Override
    public void undo() {
        previousItems.forEach(diagram::addItem);
    }
}
