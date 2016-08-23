package com.robertbalazsi.systemmodeler.command;

import com.robertbalazsi.systemmodeler.diagram.Diagram;
import com.robertbalazsi.systemmodeler.diagram.DiagramItem;

import java.util.ArrayList;
import java.util.Collection;

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
        diagram.deselectItems(new ArrayList<>(deselectedItems));
        diagram.selectItems(new ArrayList<>(selectedItems));
    }

    @Override
    public void undo() {
        diagram.deselectItems(new ArrayList<>(selectedItems));
        diagram.selectItems(new ArrayList<>(deselectedItems));
    }
}
