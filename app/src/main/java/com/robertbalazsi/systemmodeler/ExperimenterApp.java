package com.robertbalazsi.systemmodeler;

import com.robertbalazsi.systemmodeler.diagram.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
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
    private ComboBox textAlignmentComboBox;
    private ComboBox textBaselineComboBox;
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
        /*Visual rectangle = new Rectangle(200, 100);
        rectangle.setText("Rectangle");
        rectangle.setFont(new Font(30));
        rectangle.relocate(50, 100);
        Visual circle = new Circle(200);
        circle.setText("Circle");
        circle.relocate(350, 100);
        Visual ellipse = new Ellipse(200, 100);
        ellipse.setText("Ellipse");
        ellipse.relocate(400, 100);
        Visual triangle = new Triangle(200, 150);
        triangle.setText("Triangle");
        triangle.relocate(50, 300);
        Visual label = new com.robertbalazsi.systemmodeler.diagram.Label(150, 80);
        label.setText("Label");
        label.relocate(50, 450);*/
        Visual line = new Line(100, 5);
        line.relocate(350, 200);

        Scene scene = new Scene(rootPane, 900, 600);

        stage.setTitle("Experimenter App");
        stage.setScene(scene);
        stage.setMaximized(false);

        /*diagram.addItem(rectangle);
        diagram.addItem(circle);
        diagram.addItem(ellipse);
        diagram.addItem(triangle);
        diagram.addItem(label);*/
        diagram.addItem(line);

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
        pane.getChildren().add(new Label("Text alignment: "));
        textAlignmentComboBox = new ComboBox<>(FXCollections.observableArrayList("Left", "Center", "Right", "Justify"));
        pane.getChildren().add(textAlignmentComboBox);
        pane.getChildren().add(separator());
        pane.getChildren().add(new Label("Text baseline: "));
        textBaselineComboBox = new ComboBox<>(FXCollections.observableArrayList("Top", "Center", "Baseline", "Bottom"));
        pane.getChildren().add(textBaselineComboBox);
        pane.getChildren().add(separator());
        updateButton = new Button("Update");
        pane.getChildren().add(updateButton);

        updateButton.setOnAction(event -> diagram.getSelectedItems().forEach(item -> {
//            item.setText(itemTextField.getText());

            Font currentFont = item.getFont();
            item.setFont(new Font(currentFont.getName(), Long.parseLong(fontSizeTextField.getText())));

//            item.setTextFill(Color.valueOf((String)fontColorComboBox.getValue()));
        }));

        textAlignmentComboBox.setOnAction(event -> diagram.getSelectedItems().forEach(
                item -> item.setTextAlign(TextAlignment.valueOf(((String)textAlignmentComboBox.getValue()).toUpperCase()))));
        textBaselineComboBox.setOnAction(event -> diagram.getSelectedItems().forEach(
                item -> item.setTextBaseline(VPos.valueOf(((String)textBaselineComboBox.getValue()).toUpperCase()))));

        return pane;
    }

    private Separator separator() {
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));
        return separator;
    }
}
