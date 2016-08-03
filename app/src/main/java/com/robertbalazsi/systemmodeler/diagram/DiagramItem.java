package com.robertbalazsi.systemmodeler.diagram;

import com.google.common.collect.Lists;
import com.robertbalazsi.systemmodeler.controlpoint.ControlPoint;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;
import java.util.List;

/**
 * Defines an item on a diagram.
 */
public abstract class DiagramItem extends Canvas {
    private boolean isMoving = false;
    private List<ControlPoint> controlPoints = Lists.newArrayList();
    private ControlPoint selectedControlPoint;
    private boolean selected = false;

    public DiagramItem(String id, double width, double height) {
        super(width, height);
        setId(id);
        drawItem();
        controlPoints.addAll(setupControlPoints());

        this.setOnMouseClicked(event -> {
            if (isMoving) {
                isMoving = false;
            } else {
                fireEvent(new DiagramItemMouseEvent(this, DiagramItemMouseEvent.SELECTED, event));
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
            if (selectedControlPoint != null) {
                selectedControlPoint.receiveMousePressed(event);
                fireEvent(new DiagramItemMouseEvent(this, DiagramItemMouseEvent.RESIZE_STARTED, event));
            } else {
                fireEvent(new DiagramItemMouseEvent(this, DiagramItemMouseEvent.MOVE_STARTED, event));
            }
            event.consume();
        });

        this.setOnMouseDragged(event -> {
            if (selectedControlPoint != null) {
                controlPoints.forEach(point -> {
                    point.clear();
                    point.refreshBounds();
                });

                //TODO: constrain move on ctrl pressed, but make exceptions to Circle, and other inherently constrained items

                selectedControlPoint.receiveMouseDragged(event);
                clear();
                drawItem();
                drawSelectionBox();

                controlPoints.forEach(ControlPoint::deselect);
                selectedControlPoint.select();
                fireEvent(new DiagramItemMouseEvent(this, DiagramItemMouseEvent.RESIZING, event));
            } else {
                isMoving = true;
                fireEvent(new DiagramItemMouseEvent(this, DiagramItemMouseEvent.MOVING, event));
            }
            event.consume();
        });

        this.setOnMouseReleased(event -> {
            if (isMoving) {
                fireEvent(new DiagramItemMouseEvent(this, DiagramItemMouseEvent.MOVE_FINISHED, event));
            } else {
                fireEvent(new DiagramItemMouseEvent(this, DiagramItemMouseEvent.RESIZE_FINISHED, event));
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

    @Override
    public boolean equals(Object obj) {
        return !(obj == null || !getClass().equals(obj.getClass())) &&
                new EqualsBuilder().append(getId(), ((DiagramItem) obj).getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).hashCode();
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
