package com.robertbalazsi.systemmodeler.model;

import com.robertbalazsi.systemmodeler.diagram.Visual;
import com.robertbalazsi.systemmodeler.domain.Entity;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;

/**
 * Base class for visualized model items that are linkable.
 * TODO continue implementing
 */
public abstract class VisualizedLinkableItem<T extends VisualizedModelItem> extends VisualizedModelItem implements Linkable<T> {

    protected SetProperty<VisualizedInteraction> links = new SimpleSetProperty<>(this, "links", FXCollections.observableSet());

    public VisualizedLinkableItem(Entity entity, Visual visual) {
        super(entity, visual);
    }

    @Override
    public void linkFrom(T item) {

    }

    @Override
    public void linkTo(T item) {

    }

    @Override
    public void link(T item) {

    }

    @Override
    public void unlink(T item) {

    }
}
