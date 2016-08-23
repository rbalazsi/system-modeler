package com.robertbalazsi.systemmodeler.global;

import com.robertbalazsi.systemmodeler.command.Command;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

/**
 * Global component that manages the changes of a diagram, supporting Undo & Redo functionality.
 */
public class ChangeManager {

    private static final ChangeManager INSTANCE = new ChangeManager();

    private ReadOnlyListProperty<Command> undoStack = new SimpleListProperty<>(this, "undoStack", FXCollections.observableArrayList());

    public ReadOnlyListProperty<Command> undoStackProperty() {
        return undoStack;
    }

    private ReadOnlyListProperty<Command> redoStack = new SimpleListProperty<>(this, "redoStack", FXCollections.observableArrayList());

    public ReadOnlyListProperty<Command> redoStackProperty() {
        return redoStack;
    }

    private ChangeManager() {
        /* hidden - Singleton class */
    }

    public static ChangeManager getInstance() {
        return INSTANCE;
    }

    public void putCommand(Command command) {
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

    public void clearUndos() {
        undoStack.clear();
    }

    public void clearRedos() {
        redoStack.clear();
    }
}
