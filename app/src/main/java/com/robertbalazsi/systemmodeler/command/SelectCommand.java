package com.robertbalazsi.systemmodeler.command;

import com.robertbalazsi.systemmodeler.diagram.Diagram;
import com.robertbalazsi.systemmodeler.diagram.DiagramItem;

/**
 * Encapsulates the undoable selection of diagram items.
 */
public class SelectCommand implements Command {

    private final Diagram diagram;
    private final DiagramItem item;

    public SelectCommand(final Diagram diagram, final DiagramItem item) {
        this.diagram = diagram;
        this.item = item;
    }

    @Override
    public void execute() {
        diagram.select(item);
    }

    @Override
    public void undo() {
        diagram.deselect(item);
    }
}
