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

    private ConfigurableApplicationContext appContext;
    private Parent rootNode;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        appContext = SpringApplication.run(SystemModelerApp.class);

        String fxmlFile = "/fxml/mainPanel.fxml";
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(appContext::getBean);
        rootNode = loader.load(getClass().getResourceAsStream(fxmlFile));
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("System Modeler");
        Scene scene = new Scene(rootNode, 1024, 768);
        scene.getStylesheets().add("/styles/main.css");
        stage.setScene(scene);
        stage.setMaximized(false);

        stage.show();
    }

    @Override
    public void stop() throws Exception {
        appContext.close();
    }
}
