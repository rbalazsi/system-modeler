package com.robertbalazsi.systemmodeler.diagram;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import lombok.Getter;

/**
 * Encapsulates an event of a visual.
 */
public class VisualMouseEvent extends Event {

    public static EventType<VisualMouseEvent> SELECTED = new EventType<>(Event.ANY, "SELECTED");
    public static EventType<VisualMouseEvent> MOVE_STARTED = new EventType<>(Event.ANY, "MOVE_STARTED");
    public static EventType<VisualMouseEvent> MOVING = new EventType<>(Event.ANY, "MOVING");
    public static EventType<VisualMouseEvent> MOVE_FINISHED = new EventType<>(Event.ANY, "MOVE_FINISHED");
    public static EventType<VisualMouseEvent> RESIZE_STARTED = new EventType<>(Event.ANY, "RESIZE_STARTED");
    public static EventType<VisualMouseEvent> RESIZING = new EventType<>(Event.ANY, "RESIZING");
    public static EventType<VisualMouseEvent> RESIZE_FINISHED = new EventType<>(Event.ANY, "RESIZE_FINISHED");
    public static EventType<VisualMouseEvent> DRAG_COPY_STARTED = new EventType<>(Event.ANY, "DRAG_COPY_STARTED");
    public static EventType<VisualMouseEvent> DRAG_COPYING = new EventType<>(Event.ANY, "DRAG_COPYING");
    public static EventType<VisualMouseEvent> DRAG_COPY_FINISHED = new EventType<>(Event.ANY, "DRAG_COPY_FINISHED");
    public static EventType<VisualMouseEvent> DRAG_COPY_CANCELLED = new EventType<>(Event.ANY, "DRAG_COPY_CANCELLED");
    public static EventType<VisualMouseEvent> TEXT_EDITING = new EventType<>(Event.ANY, "TEXT_EDITING");
    public static EventType<VisualMouseEvent> CONTEXT_MENU = new EventType<>(Event.ANY, "CONTEXT_MENU");

    @Getter
    private MouseEvent mouseEvent;

    public VisualMouseEvent(Object source, EventType<? extends VisualMouseEvent> eventType, MouseEvent mouseEvent) {
        super(source, null, eventType);
        this.mouseEvent = mouseEvent;
    }
}
