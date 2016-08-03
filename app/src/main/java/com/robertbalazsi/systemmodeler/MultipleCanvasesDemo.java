package com.robertbalazsi.systemmodeler;

import com.robertbalazsi.systemmodeler.diagram.DiagramItem;
import com.robertbalazsi.systemmodeler.diagram.Ellipse;
import com.robertbalazsi.systemmodeler.diagram.Label;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Created by robert.balazsi on 8/1/2016.
 */
public class MultipleCanvasesDemo extends Application {

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Pane rootPane = new Pane();
        rootPane.setPrefWidth(900);
        rootPane.setPrefHeight(600);

        //TODO: Add children
//        DiagramItem item = new Rectangle(200, 100);
//        DiagramItem item = new Circle(200);
//        DiagramItem item = new Ellipse("ellipse_1", 200, 100);
        DiagramItem item = new Label("label_1", 200, 100);
        item.relocate(250, 300);

        rootPane.setOnMouseClicked(event -> {
            if (!item.getBoundsInParent().contains(event.getSceneX(), event.getSceneY())) {
                item.setSelected(false);
            }
        });

        Scene scene = new Scene(rootPane, 900, 600);

        stage.setTitle("Multiple Canvases Demo");
        stage.setScene(scene);
        stage.setMaximized(false);

        rootPane.getChildren().add(item);
        stage.show();
    }

}
