package com.robertbalazsi.systemmodeler.command;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A compound command formed by multiple commands. They are executed in the given order, and undone in reverse order.
 */
public class CompoundCommand implements Command {

    private final ArrayList<Command> commands;

    public CompoundCommand(ArrayList<Command> commands) {
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
