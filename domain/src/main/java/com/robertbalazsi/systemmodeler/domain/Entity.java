package com.robertbalazsi.systemmodeler.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * Defines a basic object to be modeled.
 */
public abstract class Entity {

    @Getter
    @Setter
    protected Long id;

    @Getter
    @Setter
    protected String name;

    @Getter
    @Setter
    protected String description;
}
