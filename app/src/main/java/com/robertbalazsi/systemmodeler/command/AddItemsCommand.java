package com.robertbalazsi.systemmodeler.command;

import com.robertbalazsi.systemmodeler.diagram.Diagram;
import com.robertbalazsi.systemmodeler.diagram.Visual;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the undoable operation of adding visuals to a diagram.
 */
public class AddItemsCommand implements Command {

    private Diagram diagram;
    private List<Visual> items;

    public AddItemsCommand(Diagram diagram, List<Visual> items) {
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
