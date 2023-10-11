package com.simplecounter;

import com.kieferlam.javafxblur.Blur;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;


public class counterApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // try (JsonReader reader = Json.createReader(new FileReader("src/main/resources/config.json"))) {
        //     JsonObject jsonObject = reader.readObject();
            
        //     String appName = jsonObject.getJsonObject("application").getString("name");
        //     String version = jsonObject.getJsonObject("application").getString("version");
        //     String appDescription = jsonObject.getJsonObject("application").getString("description");

        //     System.out.println();
        //     System.out.println(appName);
        //     System.out.println("ver. " + version);
        //     System.out.println("Description:\n" + appDescription);
        //     System.out.println();
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }

        final Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream("/counter.properties"));
        System.out.println();
        System.out.println(properties.getProperty("app.name"));
        System.out.println("ver. " + properties.getProperty("app.version"));
        System.out.println("Description:\n" + properties.getProperty("app.description"));


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

        Blur.applyBlur(primaryStage, Blur.ACRYLIC);
    }

    public static void main(String[] args) {
        Blur.loadBlurLibrary();
        launch();
    }

}