package com.robertbalazsi.systemmodeler.command;

import javafx.beans.value.WritableValue;

/**
 * Encapsulates the undoable changing of an item's property.
 */
public class PropertyChangeCommand<T> implements Command {

    private final WritableValue<T> property;
    private T value;
    private T previousValue;

    public PropertyChangeCommand(WritableValue<T> property, T value) {
        this.property = property;
        this.value = value;
        previousValue = property.getValue();
    }

    @Override
    public void execute() {
        property.setValue(value);
    }

    @Override
    public void undo() {
        property.setValue(previousValue);
    }
}
