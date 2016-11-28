package com.robertbalazsi.systemmodeler.controlpoint;

import lombok.Getter;

import java.util.List;

/**
 * A group of related control points allowing flexible resizing of their parent {@link com.robertbalazsi.systemmodeler.diagram.Visual Visual}.
 */
public class ControlPointGroup {

    @Getter
    //TODO: return immutable list?
    private final List<GroupedControlPoint> members;

    public ControlPointGroup(List<GroupedControlPoint> members) {
        this.members = members;
    }
}
