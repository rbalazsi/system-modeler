package com.robertbalazsi.systemmodeler.command;

import java.util.List;
import java.util.ListIterator;

/**
 * A compound command formed by multiple commands. They are executed in the given order, and undone in reverse order.
 */
public class CompoundCommand implements Command {

    private final List<Command> commands;

    public CompoundCommand(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public void execute() {
        commands.forEach(Command::execute);
    }

    @Override
    public void undo() {
        ListIterator<Command> iterator = commands.listIterator(commands.size());
        while (iterator.hasPrevious()) {
            iterator.previous().undo();
        }
    }
}
