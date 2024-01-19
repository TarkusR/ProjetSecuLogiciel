package fr.isep.projetseculogiciel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonToTextConverter {

    private static final String JSON_FILENAME = "security_failures.json";
    private static final String TXT_FILENAME = "security_failures.txt";

    public static void convertJsonToText() {
        try {
            // Read the JSON file
            JsonArray jsonArray = readJsonFromFile(JSON_FILENAME);

            // Write to the text file
            try (FileWriter writer = new FileWriter(TXT_FILENAME)) {
                for (JsonElement element : jsonArray) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    String formattedText = formatJsonObject(jsonObject);
                    writer.write(formattedText + System.lineSeparator());
                }
            }

            System.out.println("Conversion completed. Data written to " + TXT_FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String formatJsonObject(JsonObject jsonObject) {
        StringBuilder sb = new StringBuilder();
        sb.append("Security Failure Type: ").append(jsonObject.get("security_failure_type").getAsString()).append("\n");
        sb.append("Location: ").append(jsonObject.get("security_failure_location").getAsString()).append("\n");
        sb.append("Severity: ").append(jsonObject.get("security_failure_severity").getAsString()).append("\n");

        if (jsonObject.has("successful_password")) {
            sb.append("Successful Password: ").append(jsonObject.get("successful_password").getAsString()).append("\n");
        }

        sb.append("-----------\n");
        return sb.toString();
    }

    private static JsonArray readJsonFromFile(String filename) throws IOException {
        try (JsonReader reader = new JsonReader(new FileReader(filename))) {
            return JsonParser.parseReader(reader).getAsJsonArray();
        }
    }

    public static void main(String[] args) {
        convertJsonToText();
    }
}
