package com.robertbalazsi.systemmodeler.command;

import com.robertbalazsi.systemmodeler.diagram.DiagramItem;

/**
 * Encapsulates the undoable resizing of diagram items.
 */
public class ResizeItemCommand implements Command {

    private final DiagramItem item;
    private final ResizeState originalState;
    private final ResizeState newState;

    public ResizeItemCommand(DiagramItem item, ResizeState originalState, ResizeState newState) {
        this.item = item;
        this.originalState = originalState;
        this.newState = newState;
    }

    @Override
    public void execute() {
        item.setTranslateX(newState.translateX);
        item.setTranslateY(newState.translateY);
        item.setWidth(newState.width);
        item.setHeight(newState.height);
    }

    @Override
    public void undo() {
        item.setTranslateX(originalState.translateX);
        item.setTranslateY(originalState.translateY);
        item.setWidth(originalState.width);
        item.setHeight(originalState.height);
    }

    public static class ResizeState {
        private final double translateX;
        private final double translateY;
        private final double width;
        private final double height;

        public ResizeState(double translateX, double translateY, double width, double height) {
            this.translateX = translateX;
            this.translateY = translateY;
            this.width = width;
            this.height = height;
        }
    }
}
