package com.robertbalazsi.systemmodeler.global;

import com.robertbalazsi.systemmodeler.command.Command;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;

/**
 * Global component that manages the changes of a diagram, supporting Undo & Redo functionality.
 */
public class ChangeManager {

    private static final ChangeManager INSTANCE = new ChangeManager();

    private ReadOnlyListProperty<Command> undoStack = new SimpleListProperty<>(this, "undoStack");

    public ReadOnlyListProperty<Command> undoStackProperty() {
        return undoStack;
    }

    private ListProperty<Command> redoStack = new SimpleListProperty<>(this, "redoStack");

    public ListProperty<Command> redoStackProperty() {
        return redoStack;
    }

    private ChangeManager() {
        /* hidden - Singleton class */
    }

    public static ChangeManager getInstance() {
        return INSTANCE;
    }

    public void putCommand(Command command) {
        undoStack.add(command);
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
        undoStack.add(redo);
        return redo;
    }

    public void clearUndos() {
        undoStack.clear();
    }

    public void clearRedos() {
        redoStack.clear();
    }
}
