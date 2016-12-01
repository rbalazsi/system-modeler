package com.robertbalazsi.systemmodeler.controlpoint;

import com.robertbalazsi.systemmodeler.diagram.Visual;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

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
            group.getMembers().add(this);
        }
        this.group = group;
    }

    @Getter
    private double x;

    @Getter
    private double y;

    protected GroupedControlPoint(Visual parent, double x, double y, boolean moveConstrained, double size,
                                  Color selectedColor, Color deselectedColor) {
        super(parent, moveConstrained, size, selectedColor, deselectedColor);
        this.x = x;
        this.y = y;
    }

    @Override
    public void receiveMouseDragged(MouseEvent event) {
        double deltaX = event.getSceneX() - initMouseX;
        double deltaY = event.getSceneY() - initMouseY;

        boolean leftMost = true, rightMost = true, topMost = true, bottomMost = true;
        List<GroupedControlPoint> otherPoints = group.getMembers()
                .stream()
                .filter(member -> member != this)
                .collect(Collectors.toList());

        for (GroupedControlPoint point : otherPoints) {
            double pointX = point.getX();
            double pointY = point.getY();

            leftMost = x + deltaX < pointX;
            rightMost = x + deltaX > pointX;
            topMost = y + deltaY < pointY;
            bottomMost = y + deltaY > pointY;
        }

        if (leftMost) {
            parent.setTranslateX(parent.getTranslateX() + deltaX);
            parent.setWidth(parent.getWidth() - deltaX);
            otherPoints.forEach(point -> point.x -= deltaX);
            System.out.println(String.format("LEFTMOST - parent new translateX = %f,  new width = %f", parent.getTranslateX(), parent.getWidth()));
        } else if (rightMost) {
            x += deltaX;
            parent.setWidth(parent.getWidth() + deltaX);
            System.out.println(String.format("RIGHTMOST - parent new width = %f", parent.getWidth()));
        }

        if (topMost) {
            parent.setTranslateY(parent.getTranslateY() + deltaY);
            parent.setHeight(parent.getHeight() - deltaY);
            otherPoints.forEach(point -> point.y -= deltaY);
            System.out.println(String.format("TOPMOST - parent new translateY = %f,  new height = %f", parent.getTranslateY(), parent.getHeight()));
        } else if (bottomMost) {
            y += deltaY;
            parent.setHeight(parent.getHeight() + deltaY);
            System.out.println(String.format("BOTTOMMOST - parent new height = %f", parent.getHeight()));
        }

        initMouseX = event.getSceneX();
        initMouseY = event.getSceneY();
    }

    @Override
    public Bounds calculateBounds() {
        return new BoundingBox(x, y, getSize(), getSize());
    }

    public static class Builder extends ControlPoint.Builder<Builder, GroupedControlPoint> {

        private final double x;
        private final double y;

        public Builder(Visual parent, double x, double y) {
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
