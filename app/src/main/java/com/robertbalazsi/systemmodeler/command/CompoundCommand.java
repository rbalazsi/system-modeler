package com.robertbalazsi.systemmodeler.command;

import java.util.Collections;
import java.util.List;

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
        commands.stream().sorted(Collections.reverseOrder()).forEach(Command::undo);
    }
}
