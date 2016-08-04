package com.robertbalazsi.systemmodeler.diagram;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import lombok.Getter;

/**
 * Encapsulates an event of a diagram item.
 */
public class DiagramItemMouseEvent extends Event {

    public static EventType<DiagramItemMouseEvent> SELECTED = new EventType<>(Event.ANY, "SELECTED");
    public static EventType<DiagramItemMouseEvent> MOVE_STARTED = new EventType<>(Event.ANY, "MOVE_STARTED");
    public static EventType<DiagramItemMouseEvent> MOVING = new EventType<>(Event.ANY, "MOVING");
    public static EventType<DiagramItemMouseEvent> MOVE_FINISHED = new EventType<>(Event.ANY, "MOVE_FINISHED");
    public static EventType<DiagramItemMouseEvent> RESIZE_STARTED = new EventType<>(Event.ANY, "RESIZE_STARTED");
    public static EventType<DiagramItemMouseEvent> RESIZING = new EventType<>(Event.ANY, "RESIZING");
    public static EventType<DiagramItemMouseEvent> RESIZE_FINISHED = new EventType<>(Event.ANY, "RESIZE_FINISHED");
    public static EventType<DiagramItemMouseEvent> DOUBLE_CLICKED = new EventType<>(Event.ANY, "DOUBLE_CLICKED");

    @Getter
    private MouseEvent mouseEvent;

    public DiagramItemMouseEvent(Object source, EventType<? extends DiagramItemMouseEvent> eventType, MouseEvent mouseEvent) {
        super(source, null, eventType);
        this.mouseEvent = mouseEvent;
    }
}
