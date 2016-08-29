package com.robertbalazsi.systemmodeler.model;

import com.robertbalazsi.systemmodeler.diagram.Visual;
import com.robertbalazsi.systemmodeler.domain.Component;

/**
 * A component of the model, consisting of a domain entity, its visual representation and its links to other items.
 */
public class VisualizedComponent extends VisualizedLinkableItem<VisualizedComponent> {

    public VisualizedComponent(Component component, Visual visual) {
        super(component, visual);
    }
}
