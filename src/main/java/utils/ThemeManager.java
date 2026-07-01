package utils;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class ThemeManager {
    private static boolean isDarkTheme = true;
    
    private static final String DARK_THEME = "/css/style.css";
    private static final String LIGHT_THEME = "/css/style-light.css";

    public static boolean isDarkTheme() {
        return isDarkTheme;
    }

    public static void toggleTheme() {
        isDarkTheme = !isDarkTheme;
    }

    public static String getCurrentThemePath() {
        return isDarkTheme ? DARK_THEME : LIGHT_THEME;
    }

    public static void applyTheme(Parent root) {
        if (root != null) {
            root.getStylesheets().clear();
            root.getStylesheets().add(ThemeManager.class.getResource(getCurrentThemePath()).toExternalForm());
        }
    }
    
    public static void applyThemeToScene(Scene scene) {
        if (scene != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(ThemeManager.class.getResource(getCurrentThemePath()).toExternalForm());
        }
    }
}
