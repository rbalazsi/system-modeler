package com.robertbalazsi.systemmodeler.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Defines a basic sub-system that holds one or more entities, which can be components or sub-systems.
 */
public class SubSystem extends Entity {

    @Getter
    @Setter
    private List<Entity> children;
}
