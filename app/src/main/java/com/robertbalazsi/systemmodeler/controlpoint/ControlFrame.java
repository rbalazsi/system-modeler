package com.robertbalazsi.systemmodeler.controlpoint;

import com.robertbalazsi.systemmodeler.diagram.Visual;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A group composed of multiple control points that allow the manipulation of bounds of a Visual.
 * TODO: document types
 */
public abstract class ControlFrame {

    @Getter
    private final Visual parent;

    @Getter
    private List<ControlPoint> controlPoints;

    public ControlFrame(Visual parent, List<ControlPoint> controlPoints) {
        this.parent = parent;
        this.controlPoints = controlPoints;
    }

    static class Empty extends ControlFrame {
        public Empty(Visual parent) {
            super(parent, Collections.emptyList());
        }
    }

    /**
     * A 4-directional square frame that always maintains the aspect ratio of its parent. It is used, for example, on a
     * {@link com.robertbalazsi.systemmodeler.diagram.Circle Circle}.
     */
    static class FourDirectionalSquare extends ControlFrame {

        FourDirectionalSquare(Visual parent) {
            super(parent, Collections.unmodifiableList(Arrays.asList(
                    new PositionedControlPoint.Builder(parent, Position.TOP_LEFT).moveConstrained().build(),
                    new PositionedControlPoint.Builder(parent, Position.TOP_RIGHT).moveConstrained().build(),
                    new PositionedControlPoint.Builder(parent, Position.BOTTOM_LEFT).moveConstrained().build(),
                    new PositionedControlPoint.Builder(parent, Position.BOTTOM_RIGHT).moveConstrained().build()
            )));
        }
    }

    /**
     * A rectangular frame the allows resizing its parent from all 8 directions, not necessarily maintaining its aspect ratio.
     */
    static class EightDirectionalRectangle extends ControlFrame {

        EightDirectionalRectangle(Visual parent) {
            super(parent, Collections.unmodifiableList(Arrays.asList(
                    new PositionedControlPoint.Builder(parent, Position.TOP_LEFT).build(),
                    new PositionedControlPoint.Builder(parent, Position.TOP_CENTER).build(),
                    new PositionedControlPoint.Builder(parent, Position.TOP_RIGHT).build(),

                    new PositionedControlPoint.Builder(parent, Position.MIDDLE_LEFT).build(),
                    new PositionedControlPoint.Builder(parent, Position.MIDDLE_RIGHT).build(),

                    new PositionedControlPoint.Builder(parent, Position.BOTTOM_LEFT).build(),
                    new PositionedControlPoint.Builder(parent, Position.BOTTOM_CENTER).build(),
                    new PositionedControlPoint.Builder(parent, Position.BOTTOM_RIGHT).build()
            )));
        }
    }

    /**
     * A polygonal frame that allows resizing its parent manipulating its bounds defined as the smallest rectangle that
     * includes all of the control points of the group associated with the frame.
     */
    static class PolygonalFrame extends ControlFrame {

        public PolygonalFrame(Visual parent, ControlPointGroup controlPointGroup) {
            super(parent, controlPointGroup.getMembers().stream().collect(Collectors.toList()));
        }
    }
}
