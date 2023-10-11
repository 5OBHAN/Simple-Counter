package com.simplecounter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;


public class counterApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/counter.fxml"));
        Scene scene = new Scene(root);

        // SETTING BACKGROND TRANSPARENT TO MAKE WINDOW CORNER ROUNDED
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);

        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/assets/icon/favicon.png"))));
        
        primaryStage.setTitle("Counter");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}