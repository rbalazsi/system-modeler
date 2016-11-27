package com.robertbalazsi.systemmodeler.controlpoint;

import com.robertbalazsi.systemmodeler.diagram.Visual;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
                    new ControlPoint.Builder(parent, Location.TOP_LEFT).moveConstrained().build(),
                    new ControlPoint.Builder(parent, Location.TOP_RIGHT).moveConstrained().build(),
                    new ControlPoint.Builder(parent, Location.BOTTOM_LEFT).moveConstrained().build(),
                    new ControlPoint.Builder(parent, Location.BOTTOM_RIGHT).moveConstrained().build()
            )));
        }
    }

    /**
     * A rectangular frame the allows resizing its parent from all 8 directions, not necessarily maintaining its aspect ratio.
     */
    static class EightDirectionalRectangle extends ControlFrame {

        EightDirectionalRectangle(Visual parent) {
            super(parent, Collections.unmodifiableList(Arrays.asList(
                    new ControlPoint.Builder(parent, Location.TOP_LEFT).build(),
                    new ControlPoint.Builder(parent, Location.TOP_CENTER).build(),
                    new ControlPoint.Builder(parent, Location.TOP_RIGHT).build(),

                    new ControlPoint.Builder(parent, Location.MIDDLE_LEFT).build(),
                    new ControlPoint.Builder(parent, Location.MIDDLE_RIGHT).build(),

                    new ControlPoint.Builder(parent, Location.BOTTOM_LEFT).build(),
                    new ControlPoint.Builder(parent, Location.BOTTOM_CENTER).build(),
                    new ControlPoint.Builder(parent, Location.BOTTOM_RIGHT).build()
            )));
        }
    }
}
