package com.robertbalazsi.systemmodeler.model;

import com.robertbalazsi.systemmodeler.diagram.Visual;
import com.robertbalazsi.systemmodeler.domain.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for representing an item on the model, consisting of a domain entity, its visual representation and its links to other items.
 */
public abstract class VisualizedModelItem {

    @Getter
    @Setter
    protected Entity entity;

    @Getter
    @Setter
    protected Visual visual;

    public VisualizedModelItem(Entity entity, Visual visual) {
        this.entity = entity;
        this.visual = visual;
    }
}
