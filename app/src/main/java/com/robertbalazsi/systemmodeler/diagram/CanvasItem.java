package com.robertbalazsi.systemmodeler.diagram;

import com.google.common.collect.Lists;
import com.robertbalazsi.systemmodeler.controlpoint.ControlPoint;
import com.robertbalazsi.systemmodeler.controlpoint.Location;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.List;

/**
 * Defines an item on a diagram.
 */
public abstract class CanvasItem extends Canvas {
    private boolean isMoving = false;
    private double initMouseX, initMouseY;
    private double initTranslateX, initTranslateY;

    private List<ControlPoint> controlPoints = Lists.newArrayList();
    private ControlPoint selectedControlPoint;
    private boolean selected = false;

    public CanvasItem(double width, double height) {
        super(width, height);

        drawItem();
        controlPoints.addAll(setupControlPoints());

        this.setOnMouseClicked(event -> {
            if (isMoving) {
                isMoving = false;
            } else {
                select();
            }

            event.consume();
        });

        this.setOnMouseMoved(event -> {
            if (selected) {
                double mouseX = event.getX();
                double mouseY = event.getY();
                for (ControlPoint controlPoint : controlPoints) {
                    if (controlPoint.getBounds().contains(mouseX, mouseY)) {
                        controlPoint.select();
                    } else {
                        controlPoint.deselect();
                    }
                }
            }
        });

        this.setOnMouseExited(event -> {
            if (selected) {
                controlPoints.forEach(ControlPoint::deselect);
            }
        });

        this.setOnMousePressed(event -> {
            selectedControlPoint = getSelectedControlPoint(event.getX(), event.getY());
            initMouseX = event.getSceneX();
            initMouseY = event.getSceneY();
            initTranslateX = getTranslateX();
            initTranslateY = getTranslateY();

            if (selectedControlPoint != null) {
                selectedControlPoint.receiveMousePressed(event);
            }
            event.consume();
        });

        this.setOnMouseDragged(event -> {
            if (selectedControlPoint != null) {
                controlPoints.forEach(point -> {
                    point.clear();
                    point.refreshBounds();
                });

                selectedControlPoint.receiveMouseDragged(event);
                clear();
                drawItem();
                drawSelectionBox();

                controlPoints.forEach(ControlPoint::deselect);
                selectedControlPoint.select();
            } else {
                isMoving = true;
                setTranslateX(initTranslateX + event.getSceneX() - initMouseX);
                setTranslateY(initTranslateY + event.getSceneY() - initMouseY);
            }
            event.consume();
        });
    }

    protected abstract Collection<? extends ControlPoint> setupControlPoints();

    @Override
    public boolean isResizable() {
        return true;
    }

    public void select() {
        selected = true;
        drawSelectionBox();
        controlPoints.forEach(ControlPoint::deselect);
    }

    public void deselect() {
        selected = false;
        clear();
        drawItem();
    }

    protected abstract void drawItem();

    private void drawSelectionBox() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.beginPath();
        gc.moveTo(0, 0);
        gc.lineTo(getWidth(), 0);
        gc.lineTo(getWidth(), getHeight());
        gc.lineTo(0, getHeight());
        gc.lineTo(0, 0);
        gc.closePath();
        gc.setStroke(Color.BLACK);
        gc.setLineDashes(6);
        gc.setLineWidth(2);
        gc.stroke();
        gc.save();
    }

    private void clear() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        gc.save();
    }

    private ControlPoint getSelectedControlPoint(double mouseX, double mouseY) {
        for (ControlPoint controlPoint : controlPoints) {
            if (controlPoint.getBounds().contains(mouseX, mouseY)) {
                return controlPoint;
            }
        }
        return null;
    }
}
