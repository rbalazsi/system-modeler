package com.robertbalazsi.systemmodeler.controlpoint;

import com.robertbalazsi.systemmodeler.diagram.Rectangle;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for validating the correctness and integrity of the Control Frame API.
 */
public class ControlFrameApiTest {

    private Rectangle parent = new Rectangle(200, 100);

    @Test
    public void apiTest() {
        // 8-directional rectangular frame
        ControlFrame rectangular8DirFrame = ControlFrameFactory.eightDirectionalRectangle(parent);
        List<ControlPoint> dir8FramePoints = rectangular8DirFrame.getControlPoints();
        assertEquals(8, dir8FramePoints.size());

        // 4-directional rectangular frame
        ControlFrame rectangular4DirFrame = ControlFrameFactory.fourDirectionalSquare(parent);
        List<ControlPoint> dir4FramePoints = rectangular4DirFrame.getControlPoints();
        assertEquals(4, dir4FramePoints.size());

        // control point group
        ControlPointGroup group = new ControlPointGroup(Arrays.asList(
                new GroupedControlPoint.Builder(parent, 60, 70).build(),
                new GroupedControlPoint.Builder(parent, 100, 80).build()
        ));

        // polygonal rectangular frame
        ControlFrame polygonalFrame = ControlFrameFactory.polygonalFrame(parent, group);
        assertEquals(2, polygonalFrame.getControlPoints().size());
    }
}
