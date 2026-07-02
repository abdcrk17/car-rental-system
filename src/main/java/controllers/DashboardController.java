package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import models.DashboardDAO;
import models.Location;
import models.LocationDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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
    
    @FXML private TableView<Location> recentLocationsTable;
    @FXML private TableColumn<Location, String> colLocVoiture;
    @FXML private TableColumn<Location, String> colLocClient;
    @FXML private TableColumn<Location, LocalDate> colLocFin;
    @FXML private TableColumn<Location, String> colLocStatut;

    private DashboardDAO dashboardDAO = new DashboardDAO();
    private LocationDAO locationDAO = new LocationDAO();

    @FXML
    public void initialize() {
        colLocVoiture.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVoiture().toString()));
        colLocClient.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClient().toString()));
        colLocFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colLocStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        
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
            
            // Load Recent Locations
            List<Location> recentLocs = locationDAO.getRecentLocations(5);
            recentLocationsTable.setItems(FXCollections.observableArrayList(recentLocs));
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des statistiques du dashboard: " + e.getMessage());
        }
    }
}
