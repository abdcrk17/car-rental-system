package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainPane;

    @FXML
    public void initialize() {
        // Load the default view (Dashboard)
        loadView("DashboardView.fxml");
    }

    @FXML
    public void handleMenuDashboard() {
        loadView("DashboardView.fxml");
    }

    @FXML
    public void handleMenuVoitures() {
        loadView("VoitureView.fxml");
    }

    @FXML
    public void handleMenuClients() {
        loadView("ClientView.fxml");
    }

    @FXML
    public void handleMenuLocations() {
        loadView("LocationView.fxml");
    }

    @FXML
    public void handleLogout() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/views/LoginView.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) mainPane.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.centerOnScreen();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + fxmlFile));
            Pane view = loader.load();
            
            // Initial position and opacity for animation
            view.setOpacity(0);
            view.setTranslateY(10);
            
            mainPane.setCenter(view);
            
            // Play Fade and Translate animations
            javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(150), view);
            fade.setToValue(1.0);
            
            javafx.animation.TranslateTransition translate = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(150), view);
            translate.setToY(0);
            
            fade.play();
            translate.play();
            
        } catch (IOException e) {
            System.err.println("Failed to load view: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
