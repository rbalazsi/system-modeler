package com.robertbalazsi.systemmodeler.command;

import com.robertbalazsi.systemmodeler.diagram.Diagram;
import com.robertbalazsi.systemmodeler.diagram.Visual;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Encapsulates the undoable changing of the selection on the diagram. This includes both selected and deselected items.
 */
public class SelectionChangeCommand implements Command {

    private final Diagram diagram;
    private final Collection<Visual> selectedItems;
    private final Collection<Visual> deselectedItems;

    public SelectionChangeCommand(Diagram diagram, Collection<Visual> selectedItems, Collection<Visual> deselectedItems) {
        this.diagram = diagram;
        this.selectedItems = new ArrayList<>(selectedItems);
        this.deselectedItems = new ArrayList<>(deselectedItems);
    }

    @Override
    public void execute() {
        diagram.deselectItems(deselectedItems);
        diagram.selectItems(selectedItems);
    }

    @Override
    public void undo() {
        diagram.deselectItems(new ArrayList<>(selectedItems));
        diagram.selectItems(new ArrayList<>(deselectedItems));
    }
}
