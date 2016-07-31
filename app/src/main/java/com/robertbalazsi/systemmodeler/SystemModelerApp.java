package com.robertbalazsi.systemmodeler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for System Modeler.
 */
public class SystemModelerApp extends Application {

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        String fxmlFile = "/fxml/mainPanel.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = loader.load(getClass().getResourceAsStream(fxmlFile));

        Scene scene = new Scene(rootNode, 1024, 768);
        scene.getStylesheets().add("/styles/main.css");

        stage.setTitle("System Modeler");
        stage.setScene(scene);
        stage.setMaximized(true);

        stage.show();
    }
}
