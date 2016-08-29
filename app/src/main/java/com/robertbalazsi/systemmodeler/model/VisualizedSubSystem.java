package com.robertbalazsi.systemmodeler.model;

import com.robertbalazsi.systemmodeler.diagram.Diagram;
import com.robertbalazsi.systemmodeler.diagram.Visual;
import com.robertbalazsi.systemmodeler.domain.SubSystem;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;

/**
 * A sub-system of the model, represented in different ways depending on whether it is considered as a whole, or expanded
 * to its children.<br/>
 * As a whole, it is represented by a {@link com.robertbalazsi.systemmodeler.domain.SubSystem SubSystem} domain entity and
 * a {@link Visual}, and one or more {@link VisualizedInteraction VisualizedInteractions} with other sub-systems.<br/>
 * It also maintains a hierarchy of children, each of them with their interaction with other components.
 */
public class VisualizedSubSystem extends VisualizedLinkableItem<VisualizedSubSystem> {

    private SetProperty<VisualizedLinkableItem> children = new SimpleSetProperty<>(this, "children", FXCollections.observableSet());

    private Diagram diagram;

    public VisualizedSubSystem(SubSystem subSystem, Visual visual) {
        super(subSystem, visual);
        this.diagram = new Diagram();
    }

    public void addItem(VisualizedLinkableItem item) {

    }

    public void removeItem(VisualizedLinkableItem item) {

    }
}
