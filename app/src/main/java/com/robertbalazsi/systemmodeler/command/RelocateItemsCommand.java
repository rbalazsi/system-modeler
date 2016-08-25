package com.robertbalazsi.systemmodeler.command;

import com.robertbalazsi.systemmodeler.diagram.DiagramItem;
import com.robertbalazsi.systemmodeler.global.DiagramItemRegistry;

import java.util.Map;

/**
 * Encapsulates the undoable relocation of diagram items.
 */
public class RelocateItemsCommand implements Command {

    private Map<String, ItemTranslateState> itemDeltas;

    public RelocateItemsCommand(Map<String, ItemTranslateState> itemDeltas) {
        this.itemDeltas = itemDeltas;
    }

    @Override
    public void execute() {
        for (Map.Entry<String, ItemTranslateState> itemDelta : itemDeltas.entrySet()) {
            DiagramItem item = DiagramItemRegistry.getItem(itemDelta.getKey());
            ItemTranslateState state = itemDelta.getValue();
            item.setTranslateX(state.newTranslateX);
            item.setTranslateY(state.newTranslateY);
        }
    }

    @Override
    public void undo() {
        for (Map.Entry<String, ItemTranslateState> itemDelta : itemDeltas.entrySet()) {
            DiagramItem item = DiagramItemRegistry.getItem(itemDelta.getKey());
            ItemTranslateState state = itemDelta.getValue();
            item.setTranslateX(state.originalTranslateX);
            item.setTranslateY(state.originalTranslateY);
        }
    }

    public static class ItemTranslateState {
        private final double originalTranslateX;
        private final double originalTranslateY;
        private final double newTranslateX;
        private final double newTranslateY;

        public ItemTranslateState(double originalTranslateX, double originalTranslateY, double newTranslateX, double newTranslateY) {
            this.originalTranslateX = originalTranslateX;
            this.originalTranslateY = originalTranslateY;
            this.newTranslateX = newTranslateX;
            this.newTranslateY = newTranslateY;
        }
    }
}
