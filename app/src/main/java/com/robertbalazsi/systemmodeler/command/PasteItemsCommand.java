package com.robertbalazsi.systemmodeler.command;

import com.robertbalazsi.systemmodeler.diagram.Diagram;
import com.robertbalazsi.systemmodeler.diagram.DiagramItem;
import com.robertbalazsi.systemmodeler.global.DiagramItemRegistry;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates the undoable pasting of items to a diagram. The command takes a map between the items' IDs and their desired
 * places on the diagram. Items would then be fetched from the item registry.
 */
public class PasteItemsCommand implements Command {

    private final Diagram diagram;
    private final Map<String, Diagram.ItemCoord> itemCoords;
    private Set<String> copiedItemIds = new HashSet<>();

    public PasteItemsCommand(Diagram diagram, Map<String, Diagram.ItemCoord> itemCoords) {
        this.diagram = diagram;
        this.itemCoords = itemCoords;
    }

    @Override
    public void execute() {
        copiedItemIds.clear();
        for (Map.Entry<String, Diagram.ItemCoord> itemCoord : itemCoords.entrySet()) {
            DiagramItem item = DiagramItemRegistry.getItem(itemCoord.getKey());
            DiagramItem itemCopy = item.copy();
            Diagram.ItemCoord coord = itemCoord.getValue();
            itemCopy.relocate(coord.getX(), coord.getY());
            diagram.addItem(itemCopy);
            copiedItemIds.add(itemCopy.getId());
        }
    }

    @Override
    public void undo() {
        copiedItemIds.forEach(id -> {
            DiagramItem item = DiagramItemRegistry.getItem(id);
            DiagramItemRegistry.removeItem(item.getId());
            diagram.getChildren().remove(item);
        });
    }
}
