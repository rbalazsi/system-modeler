package com.robertbalazsi.systemmodeler.command;

import com.robertbalazsi.systemmodeler.diagram.Diagram;
import com.robertbalazsi.systemmodeler.diagram.DiagramItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the undoable adding of diagram items.
 */
public class AddItemsCommand implements Command {

    private Diagram diagram;
    private List<DiagramItem> items;

    public AddItemsCommand(Diagram diagram, List<DiagramItem> items) {
        this.diagram = diagram;
        this.items = new ArrayList<>(items);
    }

    @Override
    public void execute() {
        items.forEach(diagram::addItem);
    }

    @Override
    public void undo() {
        diagram.removeItems(items);
    }
}
