package com.robertbalazsi.systemmodeler.command;

import com.robertbalazsi.systemmodeler.diagram.Diagram;
import com.robertbalazsi.systemmodeler.diagram.DiagramItem;
import com.robertbalazsi.systemmodeler.global.DiagramItemRegistry;

import java.util.Map;

/**
 * Encapsulates the undoable relocation of diagram items.
 */
public class RelocateItemsCommand implements Command {

    private Map<String, Diagram.ItemCoord> itemDeltas;

    public RelocateItemsCommand(Map<String, Diagram.ItemCoord> itemDeltas) {
        this.itemDeltas = itemDeltas;
    }

    @Override
    public void execute() {
        relocateItems(true);
    }

    @Override
    public void undo() {
        relocateItems(false);
    }

    private void relocateItems(boolean forwardDirection) {
        double directionFactor = forwardDirection ? 1 : 0;
        for (Map.Entry<String, Diagram.ItemCoord> itemDelta : itemDeltas.entrySet()) {
            DiagramItem item = DiagramItemRegistry.getItem(itemDelta.getKey());
            Diagram.ItemCoord delta = itemDelta.getValue();
            item.setTranslateX(directionFactor * delta.getX());
            item.setTranslateY(directionFactor * delta.getY());
        }
    }
}
