package com.robertbalazsi.systemmodeler.model;

import com.robertbalazsi.systemmodeler.diagram.Visual;
import com.robertbalazsi.systemmodeler.domain.Entity;

/**
 * Represents a link between two model items. The link itself has an underlying domain object and a visual representation.
 * TODO implement
 */
public class Link {

    private Entity entity;

    private Visual visual;

    private VisualizedModelItem item1;

    private VisualizedModelItem item2;
}
