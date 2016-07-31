package com.robertbalazsi.systemmodeler.palette;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
        refreshItems();
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

    private void refreshItems() {
        itemsBox.getChildren().clear();

        TreeItem<PaletteItem> rootItem = new TreeItem<>(new PaletteItem("All Items", PaletteItem.ItemType.CATEGORY));
        rootItem.setExpanded(true);
        for (PaletteItemCategory category : items) {
            TreeItem<PaletteItem> categoryTree = new TreeItem<>(new PaletteItem(category.getName(), PaletteItem.ItemType.CATEGORY));
            categoryTree.setExpanded(true);
            categoryTree.getChildren().addAll(category.getItems().stream()
                    .map(s -> new TreeItem<>(new PaletteItem(s, PaletteItem.ItemType.ITEM))).collect(Collectors.toList()));
            rootItem.getChildren().add(categoryTree);
        }
        TreeView<PaletteItem> itemsList = new TreeView<>(rootItem);
        itemsList.setCellFactory(new PaletteListCellFactory());
        itemsBox.getChildren().add(itemsList);
    }
}
