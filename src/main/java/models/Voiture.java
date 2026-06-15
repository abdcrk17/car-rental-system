package models;

public class Voiture {
    private int id;
    private String marque;
    private String modele;
    private String immatriculation;
    private int annee;
    private int kilometrage;
    private String statut;
    private double prixJournalier;

    public Voiture() {}

    public Voiture(int id, String marque, String modele, String immatriculation, int annee, int kilometrage, String statut, double prixJournalier) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.annee = annee;
        this.kilometrage = kilometrage;
        this.statut = statut;
        this.prixJournalier = prixJournalier;
    }

    public Voiture(String marque, String modele, String immatriculation, int annee, int kilometrage, String statut, double prixJournalier) {
        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.annee = annee;
        this.kilometrage = kilometrage;
        this.statut = statut;
        this.prixJournalier = prixJournalier;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }
    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }
    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }
    public int getKilometrage() { return kilometrage; }
    public void setKilometrage(int kilometrage) { this.kilometrage = kilometrage; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public double getPrixJournalier() { return prixJournalier; }
    public void setPrixJournalier(double prixJournalier) { this.prixJournalier = prixJournalier; }
    
    @Override
    public String toString() {
        return marque + " " + modele + " (" + immatriculation + ")";
    }
}
