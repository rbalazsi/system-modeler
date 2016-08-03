package com.robertbalazsi.systemmodeler.vector;

import com.robertbalazsi.systemmodeler.controlpoint.ControlPoint;
import com.robertbalazsi.systemmodeler.controlpoint.Location;
import com.robertbalazsi.systemmodeler.diagram.DiagramItem;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * TODO: design and implement the vector graphics support
 * Control point with custom position in parent. Used for drawing items with custom vector graphics.
 */
public class CustomControlPoint extends ControlPoint {
    private double positionX;
    private double positionY;

    private double initialMouseX;
    private double initialMouseY;
    private double deltaX;
    private double deltaY;

    CustomControlPoint(DiagramItem parent, boolean moveConstrained, double positionX, double positionY, double size, Color selectedColor, Color deselectedColor) {
        super(parent, Location.BOTTOM_RIGHT, moveConstrained, size, selectedColor, deselectedColor);
        this.positionX = positionX;
        this.positionY = positionY;
    }

    @Override
    public void receiveMousePressed(MouseEvent event) {
        initialMouseX = event.getSceneX();
        initialMouseY = event.getSceneY();
    }

    @Override
    public void receiveMouseDragged(MouseEvent event) {
        deltaX = event.getSceneX() - initialMouseX;
        deltaY = event.getSceneY() - initialMouseY;
    }

    //TODO: review the mechanism
    public void receiveMouseReleased(MouseEvent event) {
        positionX += deltaX;
        positionY += deltaY;
    }

    @Override
    public Bounds calculateBounds() {
        return new BoundingBox(positionX - getSize() / 2, positionY - getSize() / 2, getSize(), getSize());
    }
}
