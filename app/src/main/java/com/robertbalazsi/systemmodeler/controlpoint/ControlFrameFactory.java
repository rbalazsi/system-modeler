package com.robertbalazsi.systemmodeler.controlpoint;

import com.robertbalazsi.systemmodeler.diagram.Visual;

/**
 * Creates different control frame configurations.
 * TODO: document methods
 */
public class ControlFrameFactory {

    private ControlFrameFactory() {
        // hidden
    }

    public static ControlFrame emptyFrame(Visual parent) {
        return new ControlFrame.Empty(parent);
    }

    public static ControlFrame fourDirectionalSquare(Visual parent) {
        return new ControlFrame.FourDirectionalSquare(parent);
    }

    public static ControlFrame eightDirectionalRectangle(Visual parent) {
        return new ControlFrame.EightDirectionalRectangle(parent);
    }

    public static ControlFrame polygonalFrame(Visual parent, ControlPointGroup controlPointGroup) {

    }
}
