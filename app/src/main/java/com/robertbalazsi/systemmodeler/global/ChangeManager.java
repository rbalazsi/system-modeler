package com.robertbalazsi.systemmodeler.global;

import com.robertbalazsi.systemmodeler.command.Command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Global component that manages the changes of a diagram, supporting Undo & Redo functionality.
 */
public class ChangeManager {

    private static final ChangeManager INSTANCE = new ChangeManager();

    private Deque<Command> undoStack = new ArrayDeque<>();
    private Deque<Command> redoStack = new ArrayDeque<>();

    private ChangeManager() {
        /* hidden - Singleton class */
    }

    public static ChangeManager getInstance() {
        return INSTANCE;
    }

    public void putCommand(Command command) {
        undoStack.push(command);
    }

    public Command undoLast() {
        if (undoStack.peek() == null) {
            return null;
        }

        Command undo = undoStack.pop();
        undo.undo();
        redoStack.push(undo);
        return undo;
    }

    public Command redoLast() {
        if (redoStack.peek() == null) {
            return null;
        }

        Command redo = redoStack.pop();
        redo.execute();
        undoStack.push(redo);
        return redo;
    }

    public void clearUndos() {
        undoStack.clear();
    }

    public void clearRedos() {
        redoStack.clear();
    }
}
