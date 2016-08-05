package com.robertbalazsi.systemmodeler;

import com.robertbalazsi.systemmodeler.diagram.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
    private TextField itemTextField;
    private ComboBox fontColorComboBox;
    private Button updateButton;

    private Diagram diagram;

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane rootPane = new BorderPane();
        rootPane.setPrefWidth(1000);
        rootPane.setPrefHeight(600);

        TitledPane propertyPage = new TitledPane();
        propertyPage.setPrefWidth(200);
        propertyPage.setText("Properties");
        rootPane.setRight(propertyPage);
        diagram = new Diagram();
        rootPane.setCenter(diagram);

        propertyPage.setContent(setupPropertyPageContents());

        //TODO: Add children
//        DiagramItem rectangle = new Rectangle("rect_1", 200, 100);
//        rectangle.setText("Rectangle");
//        rectangle.relocate(250, 250);
//        rectangle.relocate(50, 70);
        DiagramItem circle = new Circle("circle_1", 200);
        circle.setText("Circle");
        circle.relocate(250, 250);
//        DiagramItem ellipse = new Ellipse("ellipse_1", 200, 100);
//        ellipse.setText("Ellipse");
//        ellipse.relocate(400, 100);
//        DiagramItem triangle = new Triangle("triangle_1", 200, 150);
//        triangle.setText("Triangle");
//        triangle.relocate(50, 300);
//        DiagramItem label = new com.robertbalazsi.systemmodeler.diagram.Label("label_1", 150, 80);
//        label.setText("Label");
//        label.relocate(50, 450);

        Scene scene = new Scene(rootPane, 900, 600);

        stage.setTitle("Experimenter App");
        stage.setScene(scene);
        stage.setMaximized(false);

//        diagram.addItem(rectangle);
        diagram.addItem(circle);
//        diagram.addItem(ellipse);
//        diagram.addItem(triangle);
//        diagram.addItem(label);

        stage.show();
    }

    private Node setupPropertyPageContents() {
        VBox pane = new VBox();
        pane.getChildren().add(new Label("Text: "));
        itemTextField = new TextField();
        pane.getChildren().add(itemTextField);
        pane.getChildren().add(separator());
        pane.getChildren().add(new Label("Font size: "));
        fontSizeTextField = new TextField();
        pane.getChildren().add(fontSizeTextField);
        pane.getChildren().add(separator());
        pane.getChildren().add(new Label("Font color: "));
        fontColorComboBox = new ComboBox<>(FXCollections.observableArrayList("Red", "Green", "Blue", "Yellow"));
        pane.getChildren().add(fontColorComboBox);
        pane.getChildren().add(separator());
        updateButton = new Button("Update");
        pane.getChildren().add(updateButton);

        updateButton.setOnAction(event -> {
            diagram.getSelectedItems().forEach(item -> {
                item.setText(itemTextField.getText());

                Font currentFont = item.getFont();
                item.setFont(new Font(currentFont.getName(), Long.parseLong(fontSizeTextField.getText())));

                item.setTextFill(Color.valueOf((String)fontColorComboBox.getValue()));
            });
        });

        return pane;
    }

    private Separator separator() {
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));
        return separator;
    }
}
