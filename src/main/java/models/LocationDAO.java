package models;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LocationDAO {
    private VoitureDAO voitureDAO = new VoitureDAO();
    private ClientDAO clientDAO = new ClientDAO();

    public void add(Location location) throws SQLException {
        String sql = "INSERT INTO locations (voiture_id, client_id, date_debut, date_fin, prix_total, statut) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, location.getVoitureId());
            pstmt.setInt(2, location.getClientId());
            pstmt.setString(3, location.getDateDebut().toString());
            pstmt.setString(4, location.getDateFin().toString());
            pstmt.setDouble(5, location.getPrixTotal());
            pstmt.setString(6, location.getStatut());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    location.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void update(Location location) throws SQLException {
        String sql = "UPDATE locations SET voiture_id = ?, client_id = ?, date_debut = ?, date_fin = ?, prix_total = ?, statut = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, location.getVoitureId());
            pstmt.setInt(2, location.getClientId());
            pstmt.setString(3, location.getDateDebut().toString());
            pstmt.setString(4, location.getDateFin().toString());
            pstmt.setDouble(5, location.getPrixTotal());
            pstmt.setString(6, location.getStatut());
            pstmt.setInt(7, location.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM locations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Location> getAll() throws SQLException {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM locations";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Location loc = extractLocationFromResultSet(rs);
                // Load associated entities for UI convenience
                loc.setVoiture(voitureDAO.getById(loc.getVoitureId()));
                loc.setClient(clientDAO.getById(loc.getClientId()));
                locations.add(loc);
            }
        }
        return locations;
    }

    public List<Location> getRecentLocations(int limit) throws SQLException {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM locations ORDER BY id DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Location loc = extractLocationFromResultSet(rs);
                    loc.setVoiture(voitureDAO.getById(loc.getVoitureId()));
                    loc.setClient(clientDAO.getById(loc.getClientId()));
                    locations.add(loc);
                }
            }
        }
        return locations;
    }

    private Location extractLocationFromResultSet(ResultSet rs) throws SQLException {
        return new Location(
                rs.getInt("id"),
                rs.getInt("voiture_id"),
                rs.getInt("client_id"),
                LocalDate.parse(rs.getString("date_debut")),
                LocalDate.parse(rs.getString("date_fin")),
                rs.getDouble("prix_total"),
                rs.getString("statut")
        );
    }
}
