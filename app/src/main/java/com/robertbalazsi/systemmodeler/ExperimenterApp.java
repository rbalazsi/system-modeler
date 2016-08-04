package com.robertbalazsi.systemmodeler;

import com.robertbalazsi.systemmodeler.diagram.Circle;
import com.robertbalazsi.systemmodeler.diagram.Diagram;
import com.robertbalazsi.systemmodeler.diagram.DiagramItem;
import com.robertbalazsi.systemmodeler.diagram.Ellipse;
import com.robertbalazsi.systemmodeler.diagram.Rectangle;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Application used for proof-of-concepts.
 */
public class ExperimenterApp extends Application {

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    private TextField fontSizeTextField;
    private ComboBox fontColorComboBox;
    private CheckBox showLabelCheckBox;

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane rootPane = new BorderPane();
        rootPane.setPrefWidth(1000);
        rootPane.setPrefHeight(600);

        TitledPane propertyPage = new TitledPane();
        propertyPage.setPrefWidth(200);
        propertyPage.setText("Properties");
        rootPane.setRight(propertyPage);
        propertyPage.setContent(setupPropertyPageContents());

        Diagram diagram = new Diagram();
        rootPane.setCenter(diagram);

        //TODO: Add children
        DiagramItem rectangle = new Rectangle("rect_1", 200, 100);
        rectangle.relocate(50, 200);
        DiagramItem circle = new Circle("circle_1", 200);
        circle.relocate(300, 300);
        DiagramItem ellipse = new Ellipse("ellipse_1", 200, 100);
        ellipse.relocate(400, 100);
//        DiagramItem item = new Label("label_1", 200, 100);

        Scene scene = new Scene(rootPane, 900, 600);

        stage.setTitle("Experimenter App");
        stage.setScene(scene);
        stage.setMaximized(false);

        diagram.addItem(rectangle);
        diagram.addItem(circle);
        diagram.addItem(ellipse);

        stage.show();
    }

    private Node setupPropertyPageContents() {
        VBox pane = new VBox();
        showLabelCheckBox = new CheckBox("Show label");
        pane.getChildren().add(showLabelCheckBox);
        pane.getChildren().add(separator());
        pane.getChildren().add(new Label("Font size: "));
        fontSizeTextField = new TextField();
        pane.getChildren().add(fontSizeTextField);
        pane.getChildren().add(separator());
        pane.getChildren().add(new Label("Font color: "));
        fontColorComboBox = new ComboBox<>(FXCollections.observableArrayList("Red", "Blue"));
        pane.getChildren().add(fontColorComboBox);
        pane.getChildren().add(separator());

        return pane;
    }

    private Separator separator() {
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));
        return separator;
    }
}
