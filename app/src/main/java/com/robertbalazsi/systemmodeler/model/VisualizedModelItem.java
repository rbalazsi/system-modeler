package com.robertbalazsi.systemmodeler.model;

import com.robertbalazsi.systemmodeler.diagram.Visual;
import com.robertbalazsi.systemmodeler.domain.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an item on the model, consisting of a domain entity, its visual representation and its links to other items.
 * TODO implement
 */
public class VisualizedModelItem {

    private Entity entity;

    private Visual visual;

    private List<Link> links = new ArrayList<>();

    //TODO: linkWith(..).
}
