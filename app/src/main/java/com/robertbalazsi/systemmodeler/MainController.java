package com.robertbalazsi.systemmodeler;

import com.google.common.collect.Lists;
import com.robertbalazsi.systemmodeler.command.Command;
import com.robertbalazsi.systemmodeler.diagram.Diagram;
import com.robertbalazsi.systemmodeler.global.ChangeManager;
import com.robertbalazsi.systemmodeler.palette.PaletteItemCategory;
import com.robertbalazsi.systemmodeler.global.PaletteItemRegistry;
import com.robertbalazsi.systemmodeler.palette.PaletteView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for mainPanel.fxml.
 */
public class MainController implements Initializable {

    @FXML
    private PaletteView paletteView;

    @FXML
    private Diagram diagram;

    @FXML public MenuItem undoMenuItem;
    @FXML public MenuItem redoMenuItem;
    @FXML public MenuItem copyMenuItem;
    @FXML public MenuItem pasteMenuItem;
    @FXML public MenuItem deleteMenuItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO: load real items
        fillPaletteWithDummyItems(paletteView);

        setupMenus();
    }

    private static void fillPaletteWithDummyItems(PaletteView paletteView) {
        List<PaletteItemCategory> itemCategories = Lists.newArrayList();

        PaletteItemCategory basicShapes = new PaletteItemCategory("Basic Shapes");
        basicShapes.getItems().add(PaletteItemRegistry.RECTANGLE);
        basicShapes.getItems().add(PaletteItemRegistry.ROUNDED_RECTANGLE);
        basicShapes.getItems().add(PaletteItemRegistry.CIRCLE);
        basicShapes.getItems().add(PaletteItemRegistry.ELLIPSE);
        basicShapes.getItems().add(PaletteItemRegistry.TRIANGLE);

        PaletteItemCategory text = new PaletteItemCategory("Basic Shapes");
        text.getItems().add(PaletteItemRegistry.LABEL);

        PaletteItemCategory umlClassDiagram = new PaletteItemCategory("UML - class diagram");
        umlClassDiagram.getItems().add("Class");
        umlClassDiagram.getItems().add("Interface");
        umlClassDiagram.getItems().add("Enumeration");

        PaletteItemCategory databases = new PaletteItemCategory("Databases");
        databases.getItems().add("Database");

        itemCategories.add(basicShapes);
        itemCategories.add(text);
        itemCategories.add(umlClassDiagram);
        itemCategories.add(databases);

        paletteView.setItems(itemCategories);
    }

    private void setupMenus() {
        undoMenuItem.disableProperty().bind(ChangeManager.getInstance().undoStackProperty().emptyProperty());
        undoMenuItem.setOnAction(event -> {
            Command undo = ChangeManager.getInstance().undoLast();
            undo.execute();
        });

        redoMenuItem.disableProperty().bind(ChangeManager.getInstance().redoStackProperty().emptyProperty());
        redoMenuItem.setOnAction(event -> {
            Command redo = ChangeManager.getInstance().redoLast();
            redo.execute();
        });

        copyMenuItem.disableProperty().bind(diagram.selectedItemsProperty().emptyProperty());
        copyMenuItem.setOnAction(event -> {
            diagram.copySelected();
        });

        pasteMenuItem.disableProperty().bind(diagram.itemsCopiedProperty().not());
        pasteMenuItem.setOnAction(event -> {
            diagram.pasteItems(true);
        });

        deleteMenuItem.disableProperty().bind(diagram.selectedItemsProperty().emptyProperty());
        deleteMenuItem.setOnAction(event -> {
            diagram.deleteSelected();
        });
    }
}
