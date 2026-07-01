package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import models.DashboardDAO;

import java.sql.SQLException;

public class DashboardController {

    @FXML
    private Label totalRevenueLabel;
    @FXML
    private Label activeLocationsLabel;
    @FXML
    private Label totalVoituresLabel;
    @FXML
    private Label totalClientsLabel;
    @FXML
    private PieChart carStatusChart;
    @FXML
    private VBox dashboardRoot;

    private DashboardDAO dashboardDAO = new DashboardDAO();

    @FXML
    public void initialize() {
        loadStatistics();
    }

    private void loadStatistics() {
        try {
            // Load numbers
            double revenue = dashboardDAO.getTotalRevenue();
            int activeLocations = dashboardDAO.getActiveLocationsCount();
            int totalVoitures = dashboardDAO.getTotalVoitures();
            int totalClients = dashboardDAO.getTotalClients();
            int availableVoitures = dashboardDAO.getAvailableVoituresCount();
            int rentedVoitures = totalVoitures - availableVoitures; // simple approximation

            // Set Labels
            totalRevenueLabel.setText(String.format("%.2f MAD", revenue));
            activeLocationsLabel.setText(String.valueOf(activeLocations));
            totalVoituresLabel.setText(String.valueOf(totalVoitures));
            totalClientsLabel.setText(String.valueOf(totalClients));

            // Calculate percentages
            double percDispo = totalVoitures == 0 ? 0 : (double) availableVoitures / totalVoitures * 100;
            double percLoc = totalVoitures == 0 ? 0 : (double) rentedVoitures / totalVoitures * 100;

            // Setup PieChart with percentages
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data(String.format("Disponibles (%.1f%%)", percDispo), availableVoitures),
                    new PieChart.Data(String.format("En Location (%.1f%%)", percLoc), rentedVoitures)
            );
            carStatusChart.setData(pieChartData);
            
            // Add a little animation by starting data values at 0 (optional, requires more complex thread logic, simple is fine for now)
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des statistiques du dashboard: " + e.getMessage());
        }
    }
}
