package com.robertbalazsi.systemmodeler.diagram;

/**
 * An item on a diagram that can be labeled.
 */
public abstract class LabeledItem extends DiagramItem {

    public LabeledItem(String id, double width, double height) {
        super(id, width, height);
    }
}
