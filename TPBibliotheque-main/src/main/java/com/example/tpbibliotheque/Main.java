package com.example.tpbibliotheque;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.util.Objects;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        // Icône de l'application
        stage.getIcons().add(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/com/example/tpbibliotheque/images/logo.png"))
        ));

        // Afficher d'abord le login
        showLogin();
    }

    private void showLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tpbibliotheque/Login.fxml"));
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("Connexion");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
