package com.robertbalazsi.systemmodeler.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Main application class for System Modeler.
 */
@SpringBootApplication
public class SystemModelerApp extends Application {

    private static  String[] args;

    ApplicationContext context;

    public static void main(String[] args) throws Exception {
        SystemModelerApp.args = args;

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        context = SpringApplication.run(SystemModelerApp.class, args);

        /*String fxmlFile = "/fxml/mainPanel.fxml";
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(appContext::getBean);
        rootNode = loader.load(getClass().getResourceAsStream(fxmlFile));

        stage.setTitle("System Modeler");
        Scene scene = new Scene(rootNode, 1024, 768);
        scene.getStylesheets().add("/styles/main.css");
        stage.setScene(scene);
        stage.setMaximized(false);*/

        MainController mainController = context.getBean(MainController.class);

        // Create a Scene
        Scene scene = new Scene((Parent) mainController.getMainPane());
        scene.getStylesheets().add("/styles/main.css");

        stage.setScene(scene);
        stage.show();
    }
}
