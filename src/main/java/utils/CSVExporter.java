package utils;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class CSVExporter {

    public static <T> void exportToCSV(TableView<T> table, File file) {
        if (file == null) return;

        try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
            // Write BOM for Excel UTF-8 compatibility
            writer.write('\ufeff');

            // Write Headers
            StringBuilder headers = new StringBuilder();
            for (TableColumn<T, ?> col : table.getColumns()) {
                headers.append(escapeSpecialCharacters(col.getText())).append(";");
            }
            writer.println(headers.toString());

            // Write Data Rows
            for (T row : table.getItems()) {
                StringBuilder rowString = new StringBuilder();
                for (TableColumn<T, ?> col : table.getColumns()) {
                    Object cellData = col.getCellData(row);
                    String data = cellData != null ? cellData.toString() : "";
                    rowString.append(escapeSpecialCharacters(data)).append(";");
                }
                writer.println(rowString.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String escapeSpecialCharacters(String data) {
        if (data == null) return "";
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(";") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
