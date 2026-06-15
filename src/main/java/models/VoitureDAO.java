package models;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VoitureDAO {

    public void add(Voiture voiture) throws SQLException {
        String sql = "INSERT INTO voitures (marque, modele, immatriculation, annee, kilometrage, statut, prix_journalier) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, voiture.getMarque());
            pstmt.setString(2, voiture.getModele());
            pstmt.setString(3, voiture.getImmatriculation());
            pstmt.setInt(4, voiture.getAnnee());
            pstmt.setInt(5, voiture.getKilometrage());
            pstmt.setString(6, voiture.getStatut());
            pstmt.setDouble(7, voiture.getPrixJournalier());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    voiture.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void update(Voiture voiture) throws SQLException {
        String sql = "UPDATE voitures SET marque = ?, modele = ?, immatriculation = ?, annee = ?, kilometrage = ?, statut = ?, prix_journalier = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, voiture.getMarque());
            pstmt.setString(2, voiture.getModele());
            pstmt.setString(3, voiture.getImmatriculation());
            pstmt.setInt(4, voiture.getAnnee());
            pstmt.setInt(5, voiture.getKilometrage());
            pstmt.setString(6, voiture.getStatut());
            pstmt.setDouble(7, voiture.getPrixJournalier());
            pstmt.setInt(8, voiture.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM voitures WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Voiture> getAll() throws SQLException {
        List<Voiture> voitures = new ArrayList<>();
        String sql = "SELECT * FROM voitures";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                voitures.add(extractVoitureFromResultSet(rs));
            }
        }
        return voitures;
    }

    public Voiture getById(int id) throws SQLException {
        String sql = "SELECT * FROM voitures WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractVoitureFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Recherche des voitures disponibles à une date précise.
     * C'est-à-dire les voitures qui n'ont pas de location en cours chevauchant cette date.
     */
    public List<Voiture> getDisponiblesByDate(LocalDate date) throws SQLException {
        List<Voiture> voitures = new ArrayList<>();
        // Les dates en SQLite au format YYYY-MM-DD peuvent être comparées comme des strings
        String sql = "SELECT * FROM voitures WHERE statut = 'Disponible' OR id NOT IN (" +
                     "SELECT voiture_id FROM locations WHERE ? BETWEEN date_debut AND date_fin AND statut = 'En cours'" +
                     ")";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date.toString()); // LocalDate.toString() retourne le format YYYY-MM-DD
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    voitures.add(extractVoitureFromResultSet(rs));
                }
            }
        }
        return voitures;
    }

    private Voiture extractVoitureFromResultSet(ResultSet rs) throws SQLException {
        return new Voiture(
                rs.getInt("id"),
                rs.getString("marque"),
                rs.getString("modele"),
                rs.getString("immatriculation"),
                rs.getInt("annee"),
                rs.getInt("kilometrage"),
                rs.getString("statut"),
                rs.getDouble("prix_journalier")
        );
    }
}
