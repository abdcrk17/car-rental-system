CREATE TABLE IF NOT EXISTS voitures (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    marque TEXT NOT NULL,
    modele TEXT NOT NULL,
    immatriculation TEXT UNIQUE NOT NULL,
    annee INTEGER NOT NULL,
    kilometrage INTEGER NOT NULL,
    statut TEXT NOT NULL DEFAULT 'Disponible',
    prix_journalier REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS clients (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    prenom TEXT NOT NULL,
    cin TEXT UNIQUE NOT NULL,
    telephone TEXT,
    permis TEXT UNIQUE
);

CREATE TABLE IF NOT EXISTS locations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    voiture_id INTEGER NOT NULL,
    client_id INTEGER NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    prix_total REAL NOT NULL,
    statut TEXT NOT NULL DEFAULT 'En cours',
    FOREIGN KEY(voiture_id) REFERENCES voitures(id),
    FOREIGN KEY(client_id) REFERENCES clients(id)
);
