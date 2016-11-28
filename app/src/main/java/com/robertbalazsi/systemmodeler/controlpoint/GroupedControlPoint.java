package com.robertbalazsi.systemmodeler.controlpoint;

import com.robertbalazsi.systemmodeler.diagram.Visual;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * A control point that is a member of a group.
 * TODO implement
 */
public class GroupedControlPoint extends ControlPoint {

    private ControlPointGroup group;

    protected GroupedControlPoint(Visual parent, Position position, boolean moveConstrained, double size, Color selectedColor, Color deselectedColor) {
        super(parent, position, moveConstrained, size, selectedColor, deselectedColor);
    }

    @Override
    public void receiveMouseDragged(MouseEvent event) {

    }

    @Override
    public Bounds calculateBounds() {
        return null;
    }

    //TODO implement
    public static class Builder extends ControlPoint.Builder<Builder, GroupedControlPoint> {

        public Builder(Visual parent, int x, int y) {
            super(parent);
        }

        @Override
        public GroupedControlPoint build() {
            //TODO: implement with covariant return
        }
    }
}
