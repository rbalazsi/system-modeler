package com.robertbalazsi.systemmodeler;

import com.google.common.collect.Lists;
import com.robertbalazsi.systemmodeler.palette.PaletteItem;
import com.robertbalazsi.systemmodeler.palette.PaletteItemCategory;
import com.robertbalazsi.systemmodeler.palette.PaletteView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for mainPanel.fxml.
 */
public class MainController implements Initializable {

    @FXML
    private PaletteView paletteView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO: load real items
        fillPaletteWithDummyItems(paletteView);
    }

    private static void fillPaletteWithDummyItems(PaletteView paletteView) {
        List<PaletteItemCategory> itemCategories = Lists.newArrayList();

        PaletteItemCategory basicShapes = new PaletteItemCategory("Basic Shapes");
        basicShapes.getItems().add(new PaletteItem("Rectangle", null, new Rectangle(80.0, 30.0)));
        basicShapes.getItems().add(new PaletteItem("Circle", null, new Circle(15.0)));

        PaletteItemCategory umlClassDiagram = new PaletteItemCategory("UML - class diagram");
        umlClassDiagram.getItems().add(new PaletteItem("Class", null, null));
        umlClassDiagram.getItems().add(new PaletteItem("Interface", null, null));
        umlClassDiagram.getItems().add(new PaletteItem("Enumeration", null, null));

        PaletteItemCategory databases = new PaletteItemCategory("Databases");
        databases.getItems().add(new PaletteItem("Database", null, null));

        itemCategories.add(basicShapes);
        itemCategories.add(umlClassDiagram);
        itemCategories.add(databases);

        paletteView.setItems(itemCategories);
    }
}
