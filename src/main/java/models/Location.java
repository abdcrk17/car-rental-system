package models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Location {
    private int id;
    private int voitureId;
    private int clientId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private double prixTotal;
    private String statut;

    // References to actual objects for convenience in the UI
    private Voiture voiture;
    private Client client;

    public Location() {}

    public Location(int id, int voitureId, int clientId, LocalDate dateDebut, LocalDate dateFin, double prixTotal, String statut) {
        this.id = id;
        this.voitureId = voitureId;
        this.clientId = clientId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prixTotal = prixTotal;
        this.statut = statut;
    }

    public Location(int voitureId, int clientId, LocalDate dateDebut, LocalDate dateFin, double prixTotal, String statut) {
        this.voitureId = voitureId;
        this.clientId = clientId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prixTotal = prixTotal;
        this.statut = statut;
    }

    // Calcul automatique du prix total en fonction du prix journalier de la voiture et de la durée
    public void calculerPrixTotal(double prixJournalier) {
        if (dateDebut != null && dateFin != null) {
            long jours = ChronoUnit.DAYS.between(dateDebut, dateFin);
            if (jours < 1) jours = 1; // Au moins un jour de location
            this.prixTotal = jours * prixJournalier;
        }
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getVoitureId() { return voitureId; }
    public void setVoitureId(int voitureId) { this.voitureId = voitureId; }
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(double prixTotal) { this.prixTotal = prixTotal; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Voiture getVoiture() { return voiture; }
    public void setVoiture(Voiture voiture) { this.voiture = voiture; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
}
