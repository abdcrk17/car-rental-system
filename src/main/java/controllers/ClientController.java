package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Client;
import models.ClientDAO;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.SQLException;

public class ClientController {

    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, Integer> colId;
    @FXML private TableColumn<Client, String> colNom;
    @FXML private TableColumn<Client, String> colPrenom;
    @FXML private TableColumn<Client, String> colCin;
    @FXML private TableColumn<Client, String> colTelephone;

    @FXML private TextField searchField;
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtCin;
    @FXML private TextField txtTelephone;
    @FXML private TextField txtPermis;

    private ClientDAO clientDAO = new ClientDAO();
    private ObservableList<Client> clientList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colCin.setCellValueFactory(new PropertyValueFactory<>("cin"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        
        FilteredList<Client> filteredData = new FilteredList<>(clientList, b -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(client -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return client.getNom().toLowerCase().contains(lowerCaseFilter) ||
                       client.getPrenom().toLowerCase().contains(lowerCaseFilter) ||
                       client.getCin().toLowerCase().contains(lowerCaseFilter);
            });
        });
        
        SortedList<Client> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(clientTable.comparatorProperty());
        clientTable.setItems(sortedData);
        
        clientTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showClientDetails(newValue));
            
        loadClients();
    }

    private void loadClients() {
        try {
            clientList.clear();
            clientList.addAll(clientDAO.getAll());
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des clients: " + e.getMessage());
        }
    }

    private void showClientDetails(Client client) {
        if (client != null) {
            txtNom.setText(client.getNom());
            txtPrenom.setText(client.getPrenom());
            txtCin.setText(client.getCin());
            txtTelephone.setText(client.getTelephone());
            txtPermis.setText(client.getPermis());
        } else {
            clearFields();
        }
    }

    @FXML
    private void handleAdd() {
        try {
            Client c = new Client(
                txtNom.getText(),
                txtPrenom.getText(),
                txtCin.getText(),
                txtTelephone.getText(),
                txtPermis.getText()
            );
            clientDAO.add(c);
            loadClients();
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur", "Veuillez vérifier les champs de saisie.");
        }
    }

    @FXML
    private void handleUpdate() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setNom(txtNom.getText());
                selected.setPrenom(txtPrenom.getText());
                selected.setCin(txtCin.getText());
                selected.setTelephone(txtTelephone.getText());
                selected.setPermis(txtPermis.getText());
                
                clientDAO.update(selected);
                loadClients();
                clearFields();
            } catch (Exception e) {
                showAlert("Erreur", "Veuillez vérifier les champs de saisie.");
            }
        }
    }

    @FXML
    private void handleDelete() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                clientDAO.delete(selected.getId());
                loadClients();
                clearFields();
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible de supprimer le client (peut-être lié à une location).");
            }
        }
    }

    @FXML
    public void handleExportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(clientTable.getScene().getWindow());
        utils.CSVExporter.exportToCSV(clientTable, file);
    }

    private void clearFields() {
        txtNom.clear();
        txtPrenom.clear();
        txtCin.clear();
        txtTelephone.clear();
        txtPermis.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
