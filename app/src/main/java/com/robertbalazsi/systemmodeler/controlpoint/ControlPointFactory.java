package com.robertbalazsi.systemmodeler.controlpoint;

import com.robertbalazsi.systemmodeler.diagram.Visual;

import java.util.List;

/**
 * Creates different types of control points. A control point can be standalone with a predefined position relative to its
 * parent, or can be part of a group (having a custom position), both of these defining the means of resizing the parent.
 */
public class ControlPointFactory {

    private ControlPointFactory() {
        // hidden
    }

    public static ControlPoint createInPosition(Visual parent, Position position) {

    }

    public static ControlPointGroup createGroup(Visual parent, List<GroupedControlPoint> members) {

    }
}
