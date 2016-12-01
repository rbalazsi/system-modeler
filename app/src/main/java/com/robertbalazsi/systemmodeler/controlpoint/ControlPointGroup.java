package com.robertbalazsi.systemmodeler.controlpoint;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * A group of related control points allowing flexible resizing of their parent {@link com.robertbalazsi.systemmodeler.diagram.Visual Visual}.
 */
public class ControlPointGroup {

    @Getter
    //TODO: return immutable list?
    private List<GroupedControlPoint> members;

    public ControlPointGroup() {
        members = new ArrayList<>();
    }

    public ControlPointGroup(List<GroupedControlPoint> points) {
        this.members = points;
        points.forEach(point -> point.setGroup(this));
    }

    public void add(GroupedControlPoint point) {
        point.setGroup(this);
        members.add(point);
    }
}
