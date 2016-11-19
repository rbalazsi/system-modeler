package com.robertbalazsi.systemmodeler.global;

import com.robertbalazsi.systemmodeler.command.Command;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import org.springframework.stereotype.Component;

/**
 * Global component that manages the changes of a diagram, supporting Undo & Redo functionality.
 */
@Component
public class ChangeManager {

    private ReadOnlyListProperty<Command> undoStack = new SimpleListProperty<>(this, "undoStack", FXCollections.observableArrayList());

    public ReadOnlyListProperty<Command> undoStackProperty() {
        return undoStack;
    }

    private ReadOnlyListProperty<Command> redoStack = new SimpleListProperty<>(this, "redoStack", FXCollections.observableArrayList());

    public ReadOnlyListProperty<Command> redoStackProperty() {
        return redoStack;
    }

    public void putCommand(Command command) {
        redoStack.clear();
        undoStack.get().add(command);
    }

    public Command undoLast() {
        if (undoStack.isEmpty()) {
            return null;
        }

        Command undo = undoStack.remove(undoStack.size() - 1);
        undo.undo();
        redoStack.add(undo);
        return undo;
    }

    public Command redoLast() {
        if (redoStack.isEmpty()) {
            return null;
        }

        Command redo = redoStack.remove(redoStack.size() - 1);
        redo.execute();
        undoStack.get().add(redo);
        return redo;
    }
}
