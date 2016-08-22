package com.robertbalazsi.systemmodeler.command;

import com.robertbalazsi.systemmodeler.diagram.Diagram;
import com.robertbalazsi.systemmodeler.diagram.DiagramItem;

import java.util.Collection;
import java.util.Iterator;

/**
 * Encapsulates the undoable changing of the selection on the diagram. This includes both selected and deselected items.
 */
public class SelectionChangeCommand implements Command {

    private final Diagram diagram;
    private final Collection<DiagramItem> selectedItems;
    private final Collection<DiagramItem> deselectedItems;

    public SelectionChangeCommand(Diagram diagram, Collection<DiagramItem> selectedItems, Collection<DiagramItem> deselectedItems) {
        this.diagram = diagram;
        this.selectedItems = selectedItems;
        this.deselectedItems = deselectedItems;
    }

    @Override
    public void execute() {
        diagram.selectItems(selectedItems);
        diagram.deselectItems(deselectedItems);
    }

    @Override
    public void undo() {
        diagram.selectItems(deselectedItems);
        diagram.deselectItems(selectedItems);
    }
}
