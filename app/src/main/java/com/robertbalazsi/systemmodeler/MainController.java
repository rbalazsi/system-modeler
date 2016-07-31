package com.robertbalazsi.systemmodeler;

import com.google.common.collect.Lists;
import com.robertbalazsi.systemmodeler.diagram.Diagram;
import com.robertbalazsi.systemmodeler.palette.PaletteItemCategory;
import com.robertbalazsi.systemmodeler.global.PaletteItemRegistry;
import com.robertbalazsi.systemmodeler.palette.PaletteView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO: load real items
        fillPaletteWithDummyItems(paletteView);
    }

    private static void fillPaletteWithDummyItems(PaletteView paletteView) {
        List<PaletteItemCategory> itemCategories = Lists.newArrayList();

        PaletteItemCategory basicShapes = new PaletteItemCategory("Basic Shapes");
        basicShapes.getItems().add(PaletteItemRegistry.RECTANGLE);
        basicShapes.getItems().add(PaletteItemRegistry.CIRCLE);

        PaletteItemCategory umlClassDiagram = new PaletteItemCategory("UML - class diagram");
        umlClassDiagram.getItems().add("Class");
        umlClassDiagram.getItems().add("Interface");
        umlClassDiagram.getItems().add("Enumeration");

        PaletteItemCategory databases = new PaletteItemCategory("Databases");
        databases.getItems().add("Database");

        itemCategories.add(basicShapes);
        itemCategories.add(umlClassDiagram);
        itemCategories.add(databases);

        paletteView.setItems(itemCategories);
    }
}
