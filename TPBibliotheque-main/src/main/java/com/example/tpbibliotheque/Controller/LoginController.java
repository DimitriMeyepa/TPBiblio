package com.example.tpbibliotheque.Controller;

import com.example.tpbibliotheque.DAO.UserDAO;
import com.example.tpbibliotheque.model.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.util.Duration;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Label loadingLabel; // Label pour animation des points

    private UserDAO userDAO = new UserDAO();
    private Timeline loadingAnimation;

    @FXML
    void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs !");
            return;
        }

        // Démarre l'animation des points
        startLoadingAnimation();

        // Traitement login en background pour ne pas bloquer l'UI
        new Thread(() -> {
            User user = userDAO.login(username, password);

            // Mise à jour de l'UI
            javafx.application.Platform.runLater(() -> {
                stopLoadingAnimation();
                if (user != null) {
                    openDashboard();
                    ((Stage) loginButton.getScene().getWindow()).close();
                } else {
                    showError("Nom d'utilisateur ou mot de passe incorrect !");
                }
            });
        }).start();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    }

    private void startLoadingAnimation() {
        loadingLabel.setText("");
        loadingAnimation = new Timeline(
                new KeyFrame(Duration.millis(500), e -> loadingLabel.setText(".")),
                new KeyFrame(Duration.millis(1000), e -> loadingLabel.setText("..")),
                new KeyFrame(Duration.millis(1500), e -> loadingLabel.setText("..."))
        );
        loadingAnimation.setCycleCount(Timeline.INDEFINITE);
        loadingAnimation.play();
    }

    private void stopLoadingAnimation() {
        if (loadingAnimation != null) {
            loadingAnimation.stop();
            loadingLabel.setText("");
        }
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tpbibliotheque/Dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Impossible d'ouvrir le dashboard.");
        }
    }
}


