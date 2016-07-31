package com.robertbalazsi.systemmodeler.palette;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The palette view. It contains multiple item categories, each with its items.
 */
public class PaletteView extends TitledPane {

    @FXML
    private VBox itemsBox;

    @Getter
    private List<PaletteItemCategory> items;

    public void setItems(List<PaletteItemCategory> items) {
        this.items = items;
        refreshItemsAccordion();
    }

    public PaletteView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/paletteView.fxml"));
        loader.setResources(ResourceBundle.getBundle("bundles.main"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void refreshItemsAccordion() {
        itemsBox.getChildren().clear();

        for (PaletteItemCategory category : items) {
            ListView<String> itemsList = new ListView<>();
            itemsList.setCellFactory(new PaletteListCellFactory());
            itemsList.setItems(FXCollections.observableList(category.getItems()));
            TitledPane categoryPane = new TitledPane(category.getName(), itemsList);
            itemsBox.getChildren().add(categoryPane);
        }
    }
}
