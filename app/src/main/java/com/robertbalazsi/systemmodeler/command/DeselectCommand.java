package com.robertbalazsi.systemmodeler.command;

import com.robertbalazsi.systemmodeler.diagram.Diagram;
import com.robertbalazsi.systemmodeler.diagram.DiagramItem;

/**
 * Encapsulates the undoable deselection of diagram items.
 */
public class DeselectCommand implements Command {

    private final Diagram diagram;
    private final DiagramItem item;

    public DeselectCommand(final Diagram diagram, final DiagramItem item) {
        this.diagram = diagram;
        this.item = item;
    }

    @Override
    public void execute() {
        diagram.deselect(item);
    }

    @Override
    public void undo() {
        diagram.select(item);
    }
}
