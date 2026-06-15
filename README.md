# CarRentalSystem

Système de location de voitures JavaFX + SQLite.

## À propos
Ce projet est une application de gestion de location de voitures développée en Java avec JavaFX pour l'interface graphique et SQLite pour la base de données embarquée.

## Technologies
- Java 21
- Maven
- JavaFX 21.0.11
- SQLite JDBC

## Structure du projet
- `pom.xml` : configuration Maven
- `src/main/java` : code source Java
- `src/main/resources` : ressources d'application
- `src/main/java/views` : fichiers FXML
- `target` : artefacts générés

## Exécution locale
1. Installer Java 21.
2. Installer Maven.
3. Depuis la racine du projet :

### Sur Linux / macOS
```bash
mvn clean compile
mvn -Djavafx.platform=linux javafx:run
```

### Sur Windows (PowerShell ou Git Bash)
```powershell
mvn clean compile
mvn -Djavafx.platform=win javafx:run
```

> Si vous utilisez une autre plateforme, adaptez `javafx.platform` à `mac`, `linux` ou `win` selon votre système.

## Construction du projet

```bash
mvn clean package
```

## Organisation GitHub
Avant de pousser vers GitHub, initialisez le dépôt si nécessaire :

```bash
git init
git add .
git commit -m "Initial commit"
```

Ensuite, ajoutez l’URL du dépôt distant :

```bash
git remote add origin https://github.com/votre-utilisateur/votre-repo.git
```

Et poussez :

```bash
git branch -M main
git push -u origin main
```

## Notes
- Ce projet est une application de bureau JavaFX. L’exécution dans un conteneur Docker est possible, mais l’affichage graphique requiert une configuration spécifique hors du conteneur.
- Le projet ne contient pas de tests unitaires actuellement.
