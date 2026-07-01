package controllers;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.UserDAO;

import java.io.IOException;

public class LoginController {

    @FXML
    private VBox loginBox;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        // Simple Fade-in animation on load
        FadeTransition fade = new FadeTransition(Duration.millis(300), loginBox);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            shakeAnimation();
            return;
        }

        if (userDAO.authenticate(username, password)) {
            loadMainView();
        } else {
            showError("Nom d'utilisateur ou mot de passe incorrect.");
            shakeAnimation();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void shakeAnimation() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), loginBox);
        tt.setByX(10f);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.playFromStart();
    }

    private void loadMainView() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/MainView.fxml"));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            
            // Add a fade transition when switching to main view
            FadeTransition fade = new FadeTransition(Duration.millis(200), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            stage.setScene(new Scene(root, 900, 600));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de l'application.");
        }
    }
}
