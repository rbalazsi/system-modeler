package com.robertbalazsi.systemmodeler.diagram;

import com.google.common.collect.Lists;
import com.robertbalazsi.systemmodeler.controlpoint.ControlPoint;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
    public static final double DEFAULT_PADDING = 3;

    private boolean isMoving = false;
    private boolean isResizing = false;
    private List<ControlPoint> controlPoints = Lists.newArrayList();
    private ControlPoint selectedControlPoint;

    private BooleanProperty resizable = new SimpleBooleanProperty(this, "resizable", true);

    public final BooleanProperty resizableProperty() {
        return resizable;
    }

    @Override
    public final boolean isResizable() {
        return resizable.get();
    }

    public final void setResizable(boolean resizable) {
        this.resizable.set(resizable);
    }

    private BooleanProperty selected = new SimpleBooleanProperty(this, "selected", false);

    public final BooleanProperty selectedProperty() {
        return selected;
    }

    public final boolean isSelected() {
        return selected.get();
    }

    public final void setSelected(boolean selected) {
        this.selected.set(selected);

    }

    public final void redraw() {
        clear();
        draw();
    }

    private DoubleProperty padding = new SimpleDoubleProperty(this, "padding", DEFAULT_PADDING);

    public final DoubleProperty paddingProperty() {
        return padding;
    }

    public final double getPadding() {
        return padding.get();
    }

    public final void setPadding(double padding) {
        this.padding.set(padding);
    }

    public DiagramItem(String id, double width, double height) {
        super(width, height);
        setId(id);

        if (isResizable()) {
            controlPoints.addAll(setupControlPoints());
        }
        resizable.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                controlPoints.addAll(setupControlPoints());
            } else {
                controlPoints.clear();
            }
            draw();
        });

        this.widthProperty().addListener(listener -> {
            redraw();
        });
        this.heightProperty().addListener(listener -> {
            redraw();
        });
        this.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                drawSelectionBox();
                controlPoints.forEach(ControlPoint::deselect);
            } else {
                redraw();
            }
        });
        this.padding.addListener(listener -> {
            redraw();
        });

        this.setOnMouseClicked(event -> {
            if (isMoving) {
                isMoving = false;
            } else if (isResizing) {
                isResizing = false;
            } else {
                fireEvent(new DiagramItemMouseEvent(this, DiagramItemMouseEvent.SELECTED, event));
            }

            event.consume();
        });

        this.setOnMouseMoved(event -> {
            if (isSelected()) {
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
            if (isSelected()) {
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
                isResizing = true;
                controlPoints.forEach(point -> {
                    point.clear();
                    point.refreshBounds();
                });

                //TODO: constrain move on ctrl pressed, but make exceptions to Circle, and other inherently constrained items

                selectedControlPoint.receiveMouseDragged(event);
                clear();
                draw();
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
    public boolean equals(Object obj) {
        return !(obj == null || !getClass().equals(obj.getClass())) &&
                new EqualsBuilder().append(getId(), ((DiagramItem) obj).getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).hashCode();
    }

    protected abstract void draw();

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
