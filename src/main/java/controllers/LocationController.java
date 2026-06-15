package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.*;

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

    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;
    @FXML private ComboBox<Voiture> comboVoiture;
    @FXML private ComboBox<Client> comboClient;
    @FXML private TextField txtPrixTotal;
    @FXML private ComboBox<String> comboStatut;

    private LocationDAO locationDAO = new LocationDAO();
    private VoitureDAO voitureDAO = new VoitureDAO();
    private ClientDAO clientDAO = new ClientDAO();
    
    private ObservableList<Location> locationList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        comboStatut.setItems(FXCollections.observableArrayList("En cours", "Terminée"));
        
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colVoiture.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getVoiture().toString()));
        colClient.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getClient().toString()));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colPrixTotal.setCellValueFactory(new PropertyValueFactory<>("prixTotal"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        
        locationTable.setItems(locationList);
        
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
            comboStatut.setValue(location.getStatut());
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
            Location loc = new Location(
                comboVoiture.getValue().getId(),
                comboClient.getValue().getId(),
                dateDebut.getValue(),
                dateFin.getValue(),
                Double.parseDouble(txtPrixTotal.getText()),
                comboStatut.getValue() != null ? comboStatut.getValue() : "En cours"
            );
            locationDAO.add(loc);
            
            // Si la location est en cours, mettre à jour le statut de la voiture
            if ("En cours".equals(loc.getStatut())) {
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
                selected.setStatut(comboStatut.getValue());
                
                locationDAO.update(selected);
                
                // Mettre à jour le statut de la voiture si la location est terminée
                if ("En cours".equals(oldStatut) && "Terminée".equals(selected.getStatut())) {
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

    private void clearFields() {
        dateDebut.setValue(null);
        dateFin.setValue(null);
        comboVoiture.setValue(null);
        comboClient.setValue(null);
        txtPrixTotal.clear();
        comboStatut.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
