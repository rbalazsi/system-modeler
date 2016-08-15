package com.robertbalazsi.systemmodeler.command;

/**
 * Defines a basic contract an undoable command.
 */
public interface Command {

    /**
     * Runs the current command. Normally, implementers would retain here the origin state of the object under state change
     * so this operation can be undone.
     */
    void execute();

    /**
     * Undoes the operation, thereby reverting the object's state back to its original state.
     */
    void undo();
}
