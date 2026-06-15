package models;

public class Client {
    private int id;
    private String nom;
    private String prenom;
    private String cin;
    private String telephone;
    private String permis;

    public Client() {}

    public Client(int id, String nom, String prenom, String cin, String telephone, String permis) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.cin = cin;
        this.telephone = telephone;
        this.permis = permis;
    }

    public Client(String nom, String prenom, String cin, String telephone, String permis) {
        this.nom = nom;
        this.prenom = prenom;
        this.cin = cin;
        this.telephone = telephone;
        this.permis = permis;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getPermis() { return permis; }
    public void setPermis(String permis) { this.permis = permis; }

    @Override
    public String toString() {
        return nom + " " + prenom + " (CIN: " + cin + ")";
    }
}
