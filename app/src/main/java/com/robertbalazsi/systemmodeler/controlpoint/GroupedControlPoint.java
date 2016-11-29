package com.robertbalazsi.systemmodeler.controlpoint;

import com.robertbalazsi.systemmodeler.diagram.Visual;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * A control point that is a member of a group.
 * TODO implement
 */
public class GroupedControlPoint extends ControlPoint {

    @Getter
    private ControlPointGroup group;

    public void setGroup(ControlPointGroup group) {
        if (this.group != null) {
            this.group.getMembers().remove(this);
        }
        group.getMembers().add(this);
        this.group = group;
    }

    @Getter
    private int x;

    @Getter
    private int y;

    protected GroupedControlPoint(Visual parent, int x, int y, boolean moveConstrained, double size,
                                  Color selectedColor, Color deselectedColor) {
        super(parent, moveConstrained, size, selectedColor, deselectedColor);
        this.x = x;
        this.y = y;
    }

    @Override
    public void receiveMouseDragged(MouseEvent event) {
        //TODO: implement
    }

    @Override
    public Bounds calculateBounds() {
        //TODO: implement
        return null;
    }

    //TODO implement
    public static class Builder extends ControlPoint.Builder<Builder, GroupedControlPoint> {

        private final int x;
        private final int y;

        public Builder(Visual parent, int x, int y) {
            super(parent);
            this.x = x;
            this.y = y;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public GroupedControlPoint build() {
            return new GroupedControlPoint(parent, x, y, moveConstrained, size, selectedColor, deselectedColor);
        }
    }
}
