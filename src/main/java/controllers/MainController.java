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
        // Load the default view (e.g., Voitures)
        loadView("VoitureView.fxml");
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

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + fxmlFile));
            Pane view = loader.load();
            mainPane.setCenter(view);
        } catch (IOException e) {
            System.err.println("Failed to load view: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
