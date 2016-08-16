package com.robertbalazsi.systemmodeler.command;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * Encapsulates the undoable copying to the system clipboard.
 */
public class CopyToClipboardCommand implements Command {

    private final ClipboardContent content;
    private ClipboardContent previousContent;

    public CopyToClipboardCommand(ClipboardContent content) {
        this.content = content;
    }

    @Override
    public void execute() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        previousContent = new ClipboardContent();
        previousContent.putString(clipboard.getString());
        Clipboard.getSystemClipboard().setContent(content);
    }

    @Override
    public void undo() {
        Clipboard.getSystemClipboard().setContent(previousContent);
    }
}
