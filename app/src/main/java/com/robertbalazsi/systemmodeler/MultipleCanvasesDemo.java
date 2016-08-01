package com.robertbalazsi.systemmodeler;

import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

/**
 * Created by robert.balazsi on 8/1/2016.
 */
public class MultipleCanvasesDemo extends Application {

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Pane rootPane = new Pane();
        rootPane.setPrefWidth(1024);
        rootPane.setPrefHeight(768);

        //TODO: Add children
        CanvasItem rectangleItem = new CanvasItem(200, 100);
        rectangleItem.relocate(250, 300);

        rootPane.setOnMouseClicked(event -> {
            if (!rectangleItem.getBoundsInParent().contains(event.getSceneX(), event.getSceneY())) {
                rectangleItem.deselect();
            }
        });

        Scene scene = new Scene(rootPane, 1024, 768);

        stage.setTitle("Multiple Canvases Demo");
        stage.setScene(scene);
        stage.setMaximized(false);

        rootPane.getChildren().add(rectangleItem);
        stage.show();
    }

    static class CanvasItem extends Canvas {
        double initMouseX, initMouseY;
        double initTranslateX, initTranslateY;
        double initX, initY;
        double initWidth, initHeight;

        private List<ControlPoint> controlPoints = Lists.newArrayList();
        private ControlPoint selectedControlPoint;
        private boolean selected = false;

        public CanvasItem(double width, double height) {
            super(width, height);

            drawItem();

            //TODO: review where to add these, and make it more general
            controlPoints.add(new ControlPoint(this, ControlPoint.Location.TOP_LEFT));
            controlPoints.add(new ControlPoint(this, ControlPoint.Location.TOP_CENTER));
            controlPoints.add(new ControlPoint(this, ControlPoint.Location.TOP_RIGHT));

            controlPoints.add(new ControlPoint(this, ControlPoint.Location.MIDDLE_LEFT));
            controlPoints.add(new ControlPoint(this, ControlPoint.Location.MIDDLE_RIGHT));

            controlPoints.add(new ControlPoint(this, ControlPoint.Location.BOTTOM_LEFT));
            controlPoints.add(new ControlPoint(this, ControlPoint.Location.BOTTOM_CENTER));
            controlPoints.add(new ControlPoint(this, ControlPoint.Location.BOTTOM_RIGHT));

            //TODO: mouse event handlers
            this.setOnMouseClicked(event -> {
                select();
            });

            this.setOnMouseMoved(event -> {
                if (selected) {
                    double mouseX = event.getX();
                    double mouseY = event.getY();
                    for (ControlPoint controlPoint : controlPoints) {
                        if (controlPoint.getBounds().contains(mouseX, mouseY)) {
                            controlPoint.select();
                        } else {
                            controlPoint.deselect(); //TODO: needed?
                        }
                    }
                }
            });

            this.setOnMouseExited(event -> {
                if (selected) {
                    controlPoints.forEach(point -> point.deselect());
                }
            });

            this.setOnMousePressed(event -> {
                selectedControlPoint = getSelectedControlPoint(event.getX(), event.getY());
                initMouseX = event.getSceneX();
                initMouseY = event.getSceneY();

                initTranslateX = getTranslateX();
                initTranslateX = getTranslateY();
                initWidth = getWidth();
                initHeight = getHeight();
                event.consume();
            });

            this.setOnMouseDragged(event -> {
                if (selectedControlPoint != null) {
                    controlPoints.forEach(point -> {
                        point.clear();
                        point.refreshBounds();
                    });

                    //TODO: THE RESIZER - refactor handling into separate methods - classes
                    if (selectedControlPoint.getLocation() == ControlPoint.Location.BOTTOM_RIGHT) {
                        setWidth(initWidth + event.getSceneX() - initMouseX);
                        setHeight(initHeight + event.getSceneY() - initMouseY);
                        clear();
                        drawItem();
                        drawSelectionBox();

                        controlPoints.forEach(point -> {
                            point.deselect();
                        });
                        selectedControlPoint.select();
                    } else if (selectedControlPoint.getLocation() == ControlPoint.Location.TOP_LEFT) {
                        setTranslateX(initTranslateX + event.getSceneX() - initMouseX);
                        setTranslateY(initTranslateY + event.getSceneY() - initMouseY);
                        setWidth(initWidth + initMouseX - event.getSceneX());
                        setHeight(initHeight + initMouseY - event.getSceneY());
                        clear();
                        drawItem();
                        drawSelectionBox();

                        controlPoints.forEach(point -> {
                            point.deselect();
                        });
                        selectedControlPoint.select();
                    }
                } else {
                    setTranslateX(initTranslateX + event.getSceneX() - initMouseX);
                    setTranslateY(initTranslateY + event.getSceneY() - initMouseY);
                }
                event.consume();
            });
        }

        @Override
        public boolean isResizable() {
            return true;
        }

        public void select() {
            selected = true;
            drawSelectionBox();
            controlPoints.forEach(point -> point.deselect());
        }

        public void deselect() {
            selected = false;
            clear();
            drawItem();
        }

        private void drawItem() {
            GraphicsContext gc = this.getGraphicsContext2D();
            gc.setFill(Color.BLUE);
            gc.setStroke(Color.TRANSPARENT);
            gc.fillRect(3, 3, getWidth() - 6, getHeight() - 6);
            gc.save();
        }

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
            gc.setLineWidth(3);
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

    static class ControlPoint {
        private static final double SIZE = 7;
        private static final Color DESELECTED_COLOR = Color.BLACK;
        private static final Color SELECTED_COLOR = Color.RED;

        //TODO: !! separate into two dimensions (horizontal, vertical), exclude MIDDLE_CENTER
        enum Location {
            TOP_LEFT,
            TOP_CENTER,
            TOP_RIGHT,
            MIDDLE_LEFT,
            MIDDLE_RIGHT,
            BOTTOM_LEFT,
            BOTTOM_CENTER,
            BOTTOM_RIGHT
        }

        private CanvasItem parent;
        private Location location;
        private Bounds bounds;

        public Location getLocation() {
            return location;
        }

        public Bounds getBounds() {
            return bounds;
        }

        public ControlPoint(CanvasItem parent, Location location) {
            this.parent = parent;
            this.location = location;
            refreshBounds();
        }

        public void select() {
            draw(SELECTED_COLOR);
        }

        public void deselect() {
            draw(DESELECTED_COLOR);
        }

        public void clear() {
            GraphicsContext gc = parent.getGraphicsContext2D();
            gc.clearRect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
            gc.save();
        }

        public void refreshBounds() {
            bounds = calculateBounds(parent, location);
        }

        private void draw(Color color) {
            GraphicsContext gc = parent.getGraphicsContext2D();
            gc.setFill(color);
            gc.fillRect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
            gc.save();
        }

        private static Bounds calculateBounds(CanvasItem parent, Location location) {
            switch (location) {
                case TOP_LEFT:
                    return new BoundingBox(0, 0, SIZE, SIZE);
                case TOP_CENTER:
                    return new BoundingBox(parent.getWidth() / 2 - SIZE/2, 0, SIZE, SIZE );
                case TOP_RIGHT:
                    return new BoundingBox(parent.getWidth()-SIZE, 0, SIZE, SIZE);
                case MIDDLE_LEFT:
                    return new BoundingBox(1, parent.getHeight() / 2 - SIZE/2, SIZE, SIZE);
                case MIDDLE_RIGHT:
                    return new BoundingBox(parent.getWidth()-SIZE, parent.getHeight() / 2 - SIZE/2, SIZE, SIZE);
                case BOTTOM_LEFT:
                    return new BoundingBox(0, parent.getHeight()-SIZE, SIZE, SIZE);
                case BOTTOM_CENTER:
                    return new BoundingBox(parent.getWidth() / 2 - SIZE/2, parent.getHeight()-SIZE, SIZE, SIZE );
                case BOTTOM_RIGHT:
                    return new BoundingBox(parent.getWidth()-SIZE, parent.getHeight()-SIZE, SIZE, SIZE);
                default:
                    return null;
            }
        }
    }
}
