package fr.isep.projetseculogiciel;

import com.google.gson.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BrokenAccessControl {

    private WebDriver driver;
    private String initialTitle = "";
    private JsonArray accessResults;
    private static final String FILENAME = "security_failures.json";

    // Constructor
    public BrokenAccessControl(String baseUrl) {
        this.accessResults = readJsonFromFile(FILENAME);
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        try {
            // Access the first URL and store its title
            initialTitle = accessUrl(baseUrl);

            // Access different URLs
            accessUrl(baseUrl + "/ftp");
            accessUrl(baseUrl + "/files");
            accessUrl(baseUrl + "/file");
            accessUrl(baseUrl + "/product");

        } finally {
            // Close the browser window
            driver.quit();
            writeJsonToFile(accessResults, FILENAME);
        }
    }

    private String accessUrl(String url) {
        driver.get(url);

        // Get the current page title
        String currentTitle = driver.getTitle();
        if (!currentTitle.equals(initialTitle)) {
            System.out.println("Navigated to: " + url);
            addResultToJSON("Broken Access Control", url, "High");
        } else {
            System.out.println("No change on the page: " + url);
        }

        // Return the current title for the first access
        return currentTitle;
    }

    private void addResultToJSON(String failureType, String location, String severity) {
        // Check if this failure has already been recorded
        for (JsonElement element : accessResults) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.get("security_failure_location").getAsString().equals(location)) {
                // Failure already recorded, do not add again
                return;
            }
        }

        JsonObject result = new JsonObject();
        result.addProperty("security_failure_type", failureType);
        result.addProperty("security_failure_location", location);
        result.addProperty("security_failure_severity", severity);

        accessResults.add(result);
    }

    private JsonArray readJsonFromFile(String filename) {
        if (Files.exists(Paths.get(filename))) {
            try (JsonReader reader = new JsonReader(new FileReader(filename))) {
                JsonElement fileContent = JsonParser.parseReader(reader);
                if (fileContent != null && fileContent.isJsonArray()) {
                    return fileContent.getAsJsonArray();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new JsonArray();
    }

    private void writeJsonToFile(JsonArray data, String filename) {
        try (FileWriter file = new FileWriter(filename, false)) {
            Gson gson = new Gson();
            String json = gson.toJson(data);
            file.write(json);
            System.out.println("Successfully written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Enter a parameter");
            return;
        }

        String baseUrl = args[0];
        new BrokenAccessControl(baseUrl);
    }
}
