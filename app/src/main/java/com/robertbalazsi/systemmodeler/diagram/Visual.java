package com.robertbalazsi.systemmodeler.diagram;

import com.google.common.collect.Lists;
import com.robertbalazsi.systemmodeler.controlpoint.ControlPoint;
import javafx.beans.property.*;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;
import java.util.List;

/**
 * Defines the visual representation of a model item.
 */
public abstract class Visual extends Canvas {
    public static final double DEFAULT_PADDING = 3;
    public static final double DEFAULT_TEXT_PADDING = 10;
    public static final TextAlignment DEFAULT_TEXT_ALIGN = TextAlignment.CENTER;
    public static final VPos DEFAULT_TEXT_BASELINE = VPos.CENTER;
    public static final Paint DEFAULT_FILL = Color.BLACK;
    public static final double DRAG_COPY_THRESHOLD = 10;

    private boolean isMoving = false;
    private boolean isResizing = false;
    private boolean isDragCopying = false;
    private double initMouseX, initMouseY;
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

    private StringProperty text = new SimpleStringProperty(this, "text");

    public final StringProperty textProperty() {
        return text;
    }

    public final String getText() {
        return text.get();
    }

    public final void setText(String text) {
        this.text.set(text);
    }

    private ObjectProperty<TextAlignment> textAlign = new SimpleObjectProperty<>(this, "textAlign", DEFAULT_TEXT_ALIGN);

    public final ObjectProperty<TextAlignment> textAlignProperty() {
        return textAlign;
    }

    public final TextAlignment getTextAlign() {
        return textAlign.get();
    }

    public final void setTextAlign(TextAlignment textAlign) {
        this.textAlign.set(textAlign);
    }

    private ObjectProperty<VPos> textBaseline = new SimpleObjectProperty<>(this, "textBaseline", DEFAULT_TEXT_BASELINE);

    public final ObjectProperty<VPos> textBaselineProperty() {
        return textBaseline;
    }

    public final VPos getTextBaseline() {
        return textBaseline.get();
    }

    public final void setTextBaseline(VPos textBaseline) {
        this.textBaseline.set(textBaseline);
    }

    private ObjectProperty<Font> font = new SimpleObjectProperty<>(this, "font", Font.getDefault());

    public final ObjectProperty<Font> fontProperty() {
        return font;
    }

    public final Font getFont() {
        return font.get();
    }

    public final void setFont(Font font) {
        this.font.set(font);
    }

    private ObjectProperty<Paint> textFill = new SimpleObjectProperty<>(this, "textFill", DEFAULT_FILL);

    public final ObjectProperty<Paint> textFillProperty() {
        return textFill;
    }

    public final Paint getTextFill() {
        return textFill.get();
    }

    public final void setTextFill(Paint textFill) {
        this.textFill.set(textFill);
    }

    private DoubleProperty textPadding = new SimpleDoubleProperty(this, "textPadding", DEFAULT_TEXT_PADDING);

    public final DoubleProperty textPaddingProperty() {
        return textPadding;
    }

    public final double getTextPadding() {
        return textPadding.get();
    }

    public final void setTextPadding(double textPadding) {
        this.textPadding.set(textPadding);
    }

    //TODO: review this for performance; since draw() is done in subclass constructors, every setter will trigger redraw
    public static void baseCopy(Visual source, Visual target) {
        target.setLayoutX(source.getLayoutX());
        target.setLayoutY(source.getLayoutY());
        target.setTranslateX(source.getTranslateX());
        target.setTranslateY(source.getTranslateY());
        target.setWidth(source.getWidth());
        target.setHeight(source.getHeight());
        target.setFont(source.getFont());
        target.setPadding(source.getPadding());
        target.setText(source.getText());
        target.setTextAlign(source.getTextAlign());
        target.setTextBaseline(source.getTextBaseline());
        target.setTextFill(source.getTextFill());
        target.setTextPadding(source.getTextPadding());
        target.setResizable(source.isResizable());
    }
    /**
     * A hook method for supporting items that always maintain their aspect ratio when resizing (for example, Circles)
     * @return True, if the item always maintains its aspect ratio, false otherwise.
     */
    public boolean alwaysMaintainsAspectRatio() {
        return false;
    }

    public Visual(double width, double height) {
        super(width, height);

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
            redraw();
        });
        this.paddingProperty().addListener(listener -> {
            redraw();
        });
        this.textProperty().addListener(listener -> {
            redraw();
        });
        this.fontProperty().addListener((observable, oldValue, newValue) -> {
            // We create a throw away Text node so JavaFX would calculate the pixel width/height of our text in the new font
            Text text = new Text(this.getText());
            text.setFont(newValue);
            Bounds bounds = text.getLayoutBounds();
            double origTranslateX = this.getTranslateX();
            double origTranslateY = this.getTranslateY();

            if (bounds.getWidth() > this.getWidth() - 2*getTextPadding() ||
                    bounds.getHeight() > this.getHeight() - 2*getTextPadding()) {
                if (bounds.getWidth() > this.getWidth() - 2*getTextPadding()) {
                    double deltaX = (this.getWidth() - (bounds.getWidth() + 2*getTextPadding())) / 2;
                    setTranslateX(origTranslateX + deltaX);
                    setWidth(bounds.getWidth()+ 2*getTextPadding());
                    if (alwaysMaintainsAspectRatio()) {
                        setTranslateY(origTranslateY + deltaX);
                        setHeight(getWidth());
                    }
                }
                if (bounds.getHeight() > this.getHeight() - 2*getTextPadding()) {
                    double deltaY = (this.getHeight() - (bounds.getHeight() + 2*getTextPadding())) / 2;
                    setTranslateY(origTranslateY + deltaY);
                    setHeight(bounds.getHeight() + 2*getTextPadding());
                    if (alwaysMaintainsAspectRatio()) {
                        setTranslateX(origTranslateX + deltaY);
                        setWidth(getHeight());
                    }
                }
            } else {
                redraw();
            }
        });
        this.textAlignProperty().addListener(listener -> {
            redraw();
        });
        this.textBaselineProperty().addListener(listener -> {
            redraw();
        });
        this.textFillProperty().addListener(listener -> {
            redraw();
        });
        this.textPaddingProperty().addListener(listener -> {
            redraw();
        });

        this.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                fireEvent(new VisualMouseEvent(this, VisualMouseEvent.CONTEXT_MENU, event));
            } else if (isMoving) {
                isMoving = false;
            } else if (isResizing) {
                isResizing = false;
            } else if (isDragCopying) {
                isDragCopying = false;
            } else if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2){
                fireEvent(new VisualMouseEvent(this, VisualMouseEvent.TEXT_EDITING, event));
            } else {
                fireEvent(new VisualMouseEvent(this, VisualMouseEvent.SELECTED, event));
            }

            event.consume();
        });

        //TODO: implement this way, not right-click event
        /*this.setOnContextMenuRequested(event -> {
            fireEvent(new DiagramItemMouseEvent(this, DiagramItemMouseEvent.CONTEXT_MENU, null));
        });*/

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
            if (event.getButton() == MouseButton.PRIMARY) {
                initMouseX = event.getSceneX();
                initMouseY = event.getSceneY();
                selectedControlPoint = getSelectedControlPoint(event.getX(), event.getY());
                if (selectedControlPoint != null) {
                    selectedControlPoint.receiveMousePressed(event);
                    fireEvent(new VisualMouseEvent(this, VisualMouseEvent.RESIZE_STARTED, event));
                } else if (!event.isShiftDown()) {
                    fireEvent(new VisualMouseEvent(this, VisualMouseEvent.MOVE_STARTED, event));
                }
            }
            event.consume();
        });

        this.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (selectedControlPoint != null) {
                    isResizing = true;
                    if (!this.alwaysMaintainsAspectRatio()) {
                        selectedControlPoint.setMoveConstrained(event.isControlDown());
                    }

                    selectedControlPoint.receiveMouseDragged(event);
                    redraw();
                    selectedControlPoint.select();
                    fireEvent(new VisualMouseEvent(this, VisualMouseEvent.RESIZING, event));
                } else if (!isDragCopying && event.isShiftDown() && isBeyondDragCopyThreshold(event)) {
                    isDragCopying = true;
                    fireEvent(new VisualMouseEvent(this, VisualMouseEvent.DRAG_COPY_STARTED, event));
                } else if (isDragCopying && event.isShiftDown()) {
                    fireEvent(new VisualMouseEvent(this, VisualMouseEvent.DRAG_COPYING, event));
                } else if (isDragCopying && !event.isShiftDown()) {
                    isDragCopying = false;
                    fireEvent(new VisualMouseEvent(this, VisualMouseEvent.DRAG_COPY_CANCELLED, event));
                } else if (!event.isShiftDown()) {
                    isMoving = true;
                    fireEvent(new VisualMouseEvent(this, VisualMouseEvent.MOVING, event));
                }
            }
            event.consume();
        });

        this.setOnMouseReleased(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                if (isMoving) {
                    fireEvent(new VisualMouseEvent(this, VisualMouseEvent.MOVE_FINISHED, event));
                } else if (isDragCopying && !event.isShiftDown()) {
                    isDragCopying = false;
                    fireEvent(new VisualMouseEvent(this, VisualMouseEvent.DRAG_COPY_CANCELLED, event));
                } else if (isDragCopying) {
                    isDragCopying = false;
                    fireEvent(new VisualMouseEvent(this, VisualMouseEvent.DRAG_COPY_FINISHED, event));
                } else if (isResizing) {
                    fireEvent(new VisualMouseEvent(this, VisualMouseEvent.RESIZE_FINISHED, event));
                }
            }
            event.consume();
        });
    }

    protected abstract Collection<? extends ControlPoint> setupControlPoints();

    public abstract Visual copy();

    protected abstract void draw();

    public final void redraw() {
        clear();
        draw();
        if (!StringUtils.isEmpty(getText())) {
            drawText();
        }
        if (isSelected()) {
            drawSelectionBox();
            controlPoints.forEach(point -> {
                point.refreshBounds();
                point.deselect();
            });
        }
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null || !getClass().equals(obj.getClass())) &&
                new EqualsBuilder().append(getId(), ((Visual) obj).getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).hashCode();
    }

    private void drawText() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.setTextAlign(getTextAlign());
        gc.setTextBaseline(getTextBaseline());
        gc.setFont(getFont());
        gc.setFill(getTextFill());
        gc.fillText(getText(), itemAlignmentToX(this), itemBaselineToY(this) );
        gc.save();
    }

    private static double itemAlignmentToX(Visual item) {
        TextAlignment align = item.getTextAlign();
        switch (align) {
            case CENTER:
                return item.getWidth() / 2;
            case RIGHT:
                return item.getWidth() - item.getTextPadding();
            default: /*LEFT, JUSTIFY*/
                return item.getTextPadding();
        }
    }

    private static double itemBaselineToY(Visual item) {
        VPos baseline = item.getTextBaseline();
        switch (baseline) {
            case TOP:
                return item.getTextPadding();
            case CENTER:
                return item.getHeight() / 2;
            case BOTTOM:
                return item.getHeight() - item.getTextPadding();
            default: /*BASELINE*/ //TODO currently same as top, review if it's worth to support it
                return item.getTextPadding();
        }
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

    private boolean isBeyondDragCopyThreshold(MouseEvent event) {
        return (Math.abs(initMouseX - event.getSceneX()) >= DRAG_COPY_THRESHOLD) ||
                Math.abs(initMouseY - event.getSceneY()) >= DRAG_COPY_THRESHOLD;
    }
}
