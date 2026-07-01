import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.DatabaseConnection;

/**
 * Classe principale de l'application Système de Location de Voitures.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize DB connection on startup
        try {
            DatabaseConnection.getConnection();
        } catch (Exception e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
        }

        // Load LoginView.fxml initially
        Parent root = FXMLLoader.load(getClass().getResource("/views/LoginView.fxml"));
        primaryStage.setTitle("Car Rental System - Mini Projet Java");
        
        // Remove window default frame for a modern look (optional, but let's stick to standard for now, just styled)
        // primaryStage.initStyle(javafx.stage.StageStyle.UNDECORATED); 
        
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    /**
     * Point d'entrée du programme.
     * @param args arguments de ligne de commande
     */
    public static void main(String[] args) {
        launch(args);
    }
}
