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

## Lancement de l'application
Vous pouvez lancer directement l'application avec Maven après compilation.

### Sous Linux / macOS
```bash
mvn -Djavafx.platform=linux javafx:run
```

### Sous Windows
```powershell
mvn -Djavafx.platform=win javafx:run
```

Si vous avez déjà empaqueté l'application avec `mvn clean package`, vous pourrez exécuter le JAR généré avec Java 21. En fonction de la façon dont vous avez construit le projet, l’utilisation de JavaFX via un JAR autonome peut nécessiter des options `--module-path` et `--add-modules`.

## Construction du projet

```bash
mvn clean package
```