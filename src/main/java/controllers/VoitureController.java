package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Voiture;
import models.VoitureDAO;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.SQLException;

public class VoitureController {

    @FXML private TableView<Voiture> voitureTable;
    @FXML private TableColumn<Voiture, Integer> colId;
    @FXML private TableColumn<Voiture, String> colMarque;
    @FXML private TableColumn<Voiture, String> colModele;
    @FXML private TableColumn<Voiture, String> colImmatriculation;
    @FXML private TableColumn<Voiture, String> colStatut;

    @FXML private TextField searchField;
    @FXML private TextField txtMarque;
    @FXML private TextField txtModele;
    @FXML private TextField txtImmatriculation;
    @FXML private TextField txtAnnee;
    @FXML private TextField txtKilometrage;
    @FXML private TextField txtPrix;
    @FXML private ComboBox<String> comboStatut;

    private VoitureDAO voitureDAO = new VoitureDAO();
    private ObservableList<Voiture> voitureList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        comboStatut.setItems(FXCollections.observableArrayList("Disponible", "Louée", "En maintenance"));
        
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        colImmatriculation.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        
        FilteredList<Voiture> filteredData = new FilteredList<>(voitureList, b -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(voiture -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return voiture.getMarque().toLowerCase().contains(lowerCaseFilter) ||
                       voiture.getModele().toLowerCase().contains(lowerCaseFilter) ||
                       voiture.getImmatriculation().toLowerCase().contains(lowerCaseFilter);
            });
        });
        
        SortedList<Voiture> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(voitureTable.comparatorProperty());
        voitureTable.setItems(sortedData);
        
        voitureTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showVoitureDetails(newValue));
            
        loadVoitures();
    }

    private void loadVoitures() {
        try {
            voitureList.clear();
            voitureList.addAll(voitureDAO.getAll());
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des voitures: " + e.getMessage());
        }
    }

    private void showVoitureDetails(Voiture voiture) {
        if (voiture != null) {
            txtMarque.setText(voiture.getMarque());
            txtModele.setText(voiture.getModele());
            txtImmatriculation.setText(voiture.getImmatriculation());
            txtAnnee.setText(String.valueOf(voiture.getAnnee()));
            txtKilometrage.setText(String.valueOf(voiture.getKilometrage()));
            txtPrix.setText(String.valueOf(voiture.getPrixJournalier()));
            comboStatut.setValue(voiture.getStatut());
        } else {
            clearFields();
        }
    }

    @FXML
    private void handleAdd() {
        try {
            Voiture v = new Voiture(
                txtMarque.getText(),
                txtModele.getText(),
                txtImmatriculation.getText(),
                Integer.parseInt(txtAnnee.getText()),
                Integer.parseInt(txtKilometrage.getText()),
                comboStatut.getValue(),
                Double.parseDouble(txtPrix.getText())
            );
            voitureDAO.add(v);
            loadVoitures();
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur", "Veuillez vérifier les champs de saisie.");
        }
    }

    @FXML
    private void handleUpdate() {
        Voiture selected = voitureTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setMarque(txtMarque.getText());
                selected.setModele(txtModele.getText());
                selected.setImmatriculation(txtImmatriculation.getText());
                selected.setAnnee(Integer.parseInt(txtAnnee.getText()));
                selected.setKilometrage(Integer.parseInt(txtKilometrage.getText()));
                selected.setPrixJournalier(Double.parseDouble(txtPrix.getText()));
                selected.setStatut(comboStatut.getValue());
                
                voitureDAO.update(selected);
                loadVoitures();
                clearFields();
            } catch (Exception e) {
                showAlert("Erreur", "Veuillez vérifier les champs de saisie.");
            }
        }
    }

    @FXML
    private void handleDelete() {
        Voiture selected = voitureTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                voitureDAO.delete(selected.getId());
                loadVoitures();
                clearFields();
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible de supprimer la voiture (peut-être liée à une location).");
            }
        }
    }

    @FXML
    public void handleExportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(voitureTable.getScene().getWindow());
        utils.CSVExporter.exportToCSV(voitureTable, file);
    }

    private void clearFields() {
        txtMarque.clear();
        txtModele.clear();
        txtImmatriculation.clear();
        txtAnnee.clear();
        txtKilometrage.clear();
        txtPrix.clear();
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
