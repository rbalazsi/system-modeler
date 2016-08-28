package com.robertbalazsi.systemmodeler.command;

import com.robertbalazsi.systemmodeler.diagram.Diagram;
import com.robertbalazsi.systemmodeler.diagram.Visual;
import com.robertbalazsi.systemmodeler.global.VisualRegistry;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates the undoable operation of pasting visuals to a diagram.. The command takes a map between the visuals' IDs
 * and their desired places on the diagram. They would then be fetched from the visual registry.
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
            Visual item = VisualRegistry.get(itemCoord.getKey());
            Visual itemCopy = item.copy();
            Diagram.ItemCoord coord = itemCoord.getValue();
            itemCopy.relocate(coord.getX(), coord.getY());
            diagram.addItem(itemCopy);
            copiedItemIds.add(itemCopy.getId());
        }
    }

    @Override
    public void undo() {
        copiedItemIds.forEach(id -> {
            Visual item = VisualRegistry.get(id);
            VisualRegistry.remove(item.getId());
            diagram.getChildren().remove(item);
        });
    }
}
