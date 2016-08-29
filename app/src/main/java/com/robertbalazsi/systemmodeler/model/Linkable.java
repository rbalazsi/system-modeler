package com.robertbalazsi.systemmodeler.model;

/**
 * Contract for uni- or bidirectional linking two model items.
 */
public interface Linkable<T extends VisualizedModelItem> {

    void linkFrom(T item);

    void linkTo(T item);

    void link(T item);

    void unlink(T item);
}
