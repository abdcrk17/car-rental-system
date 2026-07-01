package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.*;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;

public class LocationController {

    @FXML private TableView<Location> locationTable;
    @FXML private TableColumn<Location, Integer> colId;
    @FXML private TableColumn<Location, String> colVoiture;
    @FXML private TableColumn<Location, String> colClient;
    @FXML private TableColumn<Location, LocalDate> colDateDebut;
    @FXML private TableColumn<Location, LocalDate> colDateFin;
    @FXML private TableColumn<Location, Double> colPrixTotal;
    @FXML private TableColumn<Location, String> colStatut;

    @FXML private TextField searchField;
    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;
    @FXML private ComboBox<Voiture> comboVoiture;
    @FXML private ComboBox<Client> comboClient;
    @FXML private TextField txtPrixTotal;

    private LocationDAO locationDAO = new LocationDAO();
    private VoitureDAO voitureDAO = new VoitureDAO();
    private ClientDAO clientDAO = new ClientDAO();
    
    private ObservableList<Location> locationList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colVoiture.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getVoiture().toString()));
        colClient.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getClient().toString()));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colPrixTotal.setCellValueFactory(new PropertyValueFactory<>("prixTotal"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        
        FilteredList<Location> filteredData = new FilteredList<>(locationList, b -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(location -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                boolean matchVoiture = location.getVoiture() != null && location.getVoiture().toString().toLowerCase().contains(lowerCaseFilter);
                boolean matchClient = location.getClient() != null && location.getClient().toString().toLowerCase().contains(lowerCaseFilter);
                return matchVoiture || matchClient;
            });
        });
        
        SortedList<Location> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(locationTable.comparatorProperty());
        locationTable.setItems(sortedData);
        
        locationTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showLocationDetails(newValue));
            
        loadLocations();
        loadClients();
        loadToutesVoitures(); // Load all cars initially
    }

    private void loadLocations() {
        try {
            locationList.clear();
            locationList.addAll(locationDAO.getAll());
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des locations: " + e.getMessage());
        }
    }

    private void loadClients() {
        try {
            comboClient.setItems(FXCollections.observableArrayList(clientDAO.getAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadToutesVoitures() {
        try {
            comboVoiture.setItems(FXCollections.observableArrayList(voitureDAO.getAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showLocationDetails(Location location) {
        if (location != null) {
            dateDebut.setValue(location.getDateDebut());
            dateFin.setValue(location.getDateFin());
            comboVoiture.setValue(location.getVoiture());
            comboClient.setValue(location.getClient());
            txtPrixTotal.setText(String.valueOf(location.getPrixTotal()));
        } else {
            clearFields();
        }
    }

    @FXML
    private void handleRechercheDispo() {
        if (dateDebut.getValue() != null) {
            try {
                // Recherche des voitures disponibles à la date de début sélectionnée
                ObservableList<Voiture> dispos = FXCollections.observableArrayList(
                    voitureDAO.getDisponiblesByDate(dateDebut.getValue())
                );
                comboVoiture.setItems(dispos);
                if (dispos.isEmpty()) {
                    showAlert("Information", "Aucune voiture disponible à cette date.");
                } else {
                    comboVoiture.show(); // Dérouler la liste
                }
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la recherche.");
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner une date de début.");
        }
    }

    @FXML
    private void handleCalculPrix() {
        Voiture selectedCar = comboVoiture.getValue();
        LocalDate debut = dateDebut.getValue();
        LocalDate fin = dateFin.getValue();
        
        if (selectedCar != null && debut != null && fin != null) {
            Location tempLoc = new Location();
            tempLoc.setDateDebut(debut);
            tempLoc.setDateFin(fin);
            tempLoc.calculerPrixTotal(selectedCar.getPrixJournalier());
            txtPrixTotal.setText(String.valueOf(tempLoc.getPrixTotal()));
        } else {
            showAlert("Erreur", "Veuillez sélectionner une voiture et les dates.");
        }
    }

    @FXML
    private void handleAdd() {
        try {
            if (txtPrixTotal.getText().isEmpty()) {
                handleCalculPrix();
            }
            
            String calculatedStatus = LocalDate.now().isAfter(dateFin.getValue()) ? "Terminée" : "En cours";
            
            Location loc = new Location(
                comboVoiture.getValue().getId(),
                comboClient.getValue().getId(),
                dateDebut.getValue(),
                dateFin.getValue(),
                Double.parseDouble(txtPrixTotal.getText()),
                calculatedStatus
            );
            locationDAO.add(loc);
            
            // Si la location est en cours, mettre à jour le statut de la voiture
            if ("En cours".equals(calculatedStatus)) {
                Voiture v = comboVoiture.getValue();
                v.setStatut("Louée");
                voitureDAO.update(v);
            }
            
            loadLocations();
            clearFields();
            loadToutesVoitures();
        } catch (Exception e) {
            showAlert("Erreur", "Veuillez vérifier tous les champs (voiture, client, dates).");
        }
    }

    @FXML
    private void handleUpdate() {
        Location selected = locationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setVoitureId(comboVoiture.getValue().getId());
                selected.setClientId(comboClient.getValue().getId());
                selected.setDateDebut(dateDebut.getValue());
                selected.setDateFin(dateFin.getValue());
                selected.setPrixTotal(Double.parseDouble(txtPrixTotal.getText()));
                
                String oldStatut = selected.getStatut();
                String newStatut = LocalDate.now().isAfter(dateFin.getValue()) ? "Terminée" : "En cours";
                selected.setStatut(newStatut);
                
                locationDAO.update(selected);
                
                // Mettre à jour le statut de la voiture si la location est terminée
                if ("En cours".equals(oldStatut) && "Terminée".equals(newStatut)) {
                    Voiture v = comboVoiture.getValue();
                    v.setStatut("Disponible");
                    voitureDAO.update(v);
                }
                
                loadLocations();
                clearFields();
                loadToutesVoitures();
            } catch (Exception e) {
                showAlert("Erreur", "Veuillez vérifier tous les champs.");
            }
        }
    }

    @FXML
    private void handleDelete() {
        Location selected = locationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                locationDAO.delete(selected.getId());
                loadLocations();
                clearFields();
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible de supprimer la location.");
            }
        }
    }

    @FXML
    public void handleExportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(locationTable.getScene().getWindow());
        utils.CSVExporter.exportToCSV(locationTable, file);
    }

    private void clearFields() {
        dateDebut.setValue(null);
        dateFin.setValue(null);
        comboVoiture.setValue(null);
        comboClient.setValue(null);
        txtPrixTotal.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
