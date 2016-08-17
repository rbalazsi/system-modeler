package com.robertbalazsi.systemmodeler.command;

import com.robertbalazsi.systemmodeler.diagram.Diagram;
import com.robertbalazsi.systemmodeler.diagram.DiagramItem;
import com.robertbalazsi.systemmodeler.global.DiagramItemRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Encapsulates the undoable deletion of diagram items.
 */
public class DeleteCommand implements Command {

    private final Diagram diagram;
    private final List<DiagramItem> itemsToDelete;
    private List<DiagramItem> previousItems;

    public DeleteCommand(Diagram diagram, List<DiagramItem> itemsToDelete) {
        this.diagram = diagram;
        this.itemsToDelete = itemsToDelete;
        previousItems = new ArrayList<>(itemsToDelete);
    }

    @Override
    public void execute() {
        Iterator<DiagramItem> itemIterator = itemsToDelete.iterator();
        while (itemIterator.hasNext()) {
            DiagramItem item = itemIterator.next();
            DiagramItemRegistry.removeItem(item.getId());
            itemIterator.remove();
            diagram.getChildren().remove(item);
        }
    }

    @Override
    public void undo() {
        previousItems.forEach(diagram::addItem);
    }
}
