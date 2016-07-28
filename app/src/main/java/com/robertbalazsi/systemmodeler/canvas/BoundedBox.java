package com.robertbalazsi.systemmodeler.canvas;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * A bounded box drawn around a selected item.
 */
public class BoundedBox extends Group {

    private static final double CTRL_POINT_SIZE = 5.0;

    private final double x;
    private final double y;
    private final double width;
    private final double height;

    public BoundedBox(final double x, final double y, final double width, final double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // Drawing lines instead of rectangle so that mouse clicked event of the wrapped canvas object won't be hijacked
        // by its bounded box
        List<Line> mainRectLines = Arrays.asList(
                new Line(x, y, x+width, y),
                new Line(x+width, y, x+width, y+height),
                new Line(x+width, y+height, x, y+height),
                new Line(x, y+height, x, y)
        );
        mainRectLines.forEach(line -> line.setStroke(Color.BLUE));

        Rectangle rectTopLeft = new Rectangle(x-CTRL_POINT_SIZE/2, y-CTRL_POINT_SIZE/2, CTRL_POINT_SIZE, CTRL_POINT_SIZE);
        rectTopLeft.setStroke(Color.BLUE);

        Rectangle rectTopRight = new Rectangle(x+width-CTRL_POINT_SIZE/2, y-CTRL_POINT_SIZE/2, CTRL_POINT_SIZE, CTRL_POINT_SIZE);
        rectTopRight.setStroke(Color.BLUE);

        Rectangle rectBottomLeft = new Rectangle(x-CTRL_POINT_SIZE/2, y+height-CTRL_POINT_SIZE/2, CTRL_POINT_SIZE, CTRL_POINT_SIZE);
        rectBottomLeft.setStroke(Color.BLUE);

        Rectangle rectBottomRight = new Rectangle(x+width-CTRL_POINT_SIZE/2, y+height-CTRL_POINT_SIZE/2, CTRL_POINT_SIZE, CTRL_POINT_SIZE);
        rectBottomRight.setStroke(Color.BLUE);

        this.getChildren().addAll(mainRectLines);
        this.getChildren().addAll(rectTopLeft, rectTopRight, rectBottomLeft, rectBottomRight);

        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // TODO: handler for control points
                System.out.println("Bounded box clicked");
            }
        });
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(x).append(y).append(width).append(height).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BoundedBox)) {
            return false;
        }

        BoundedBox rhs = (BoundedBox)obj;
        return new EqualsBuilder().append(x, rhs.x).append(y, rhs.y).append(width, rhs.width).append(height, rhs.height)
                .isEquals();
    }
}
