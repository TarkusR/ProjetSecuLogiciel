package fr.isep.projetseculogiciel;

import com.google.gson.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class SQLInjectionController {
    private static final List<String> SQL_INJECTION_PAYLOADS = Arrays.asList(
            "' OR '1'='1'; --",
            "' AND 1=2",
            "' UNION SELECT 1, version() limit 1,1"
    );

    private WebDriver driver;
    private static final String FILENAME = "security_failures.json";

    private static JsonArray securityFailures;

    public SQLInjectionController(String url) {
        this.securityFailures = readJsonFromFile(FILENAME);
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(); // Initialize WebDriver here

        try {
            driver.get(url);
            List<WebElement> loginInputs = driver.findElements(By.cssSelector("input[name=login], input[type=text], input[type=password]"));
            for (WebElement input : loginInputs) {
                String inputName = input.getAttribute("name");
                System.out.println("Potential input field for testing: " + inputName);
                testForSqlInjection(driver, url, inputName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
            writeJsonToFile(securityFailures, FILENAME);
        }
    }

    private JsonArray readJsonFromFile(String filename) {
        JsonArray jsonArray = new JsonArray();
        if (Files.exists(Paths.get(filename))) {
            try (JsonReader reader = new JsonReader(new FileReader(filename))) {
                JsonElement fileContent = JsonParser.parseReader(reader);
                if (fileContent != null && fileContent.isJsonArray()) {
                    jsonArray = fileContent.getAsJsonArray();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }


    private static void testForSqlInjection(WebDriver driver, String baseUrl, String inputName) {
        try {
            for (String payload : SQL_INJECTION_PAYLOADS) {
                driver.get(baseUrl);

                // Fill the 'email' input with 'admin' concatenated with the payload
                WebElement emailInput = driver.findElement(By.id("email"));
                emailInput.clear();
                emailInput.sendKeys("admin" + payload);

                // Store the original URL before submitting the form
                String originalUrl = driver.getCurrentUrl();

                // Locate and fill the target input field (if necessary)
                WebElement inputField = driver.findElement(By.name(inputName));
                inputField.clear();
                inputField.sendKeys(payload);

                // Click the submit button
                driver.findElement(By.cssSelector("button[type='submit']")).click();

                // Wait for the page to load after submission
                Thread.sleep(1000); // Wait for 5 seconds

                // Check if the URL has changed
                String newUrl = driver.getCurrentUrl();
                if (!newUrl.equals(originalUrl)) {
                    System.out.println("Potential SQL injection detected for payload: " + payload);

                    JsonObject failure = new JsonObject();
                    failure.addProperty("security_failure_type", "SQL Injection");
                    failure.addProperty("security_failure_location", baseUrl);
                    failure.addProperty("security_failure_severity", "High");

                    securityFailures.add(failure);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeJsonToFile(JsonArray newData, String filename) {
        JsonArray existingData = readJsonFromFile(filename);

        // Merge newData into existingData
        for (JsonElement element : newData) {
            existingData.add(element);
        }

        // Write the merged array back to the file
        try (FileWriter file = new FileWriter(filename, false)) { // Overwrite the file
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(existingData);
            file.write(json);
            System.out.println("Successfully written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java SqlInjectionScanner <URL>");
            return;
        }
        new SQLInjectionController(args[0]);
    }



}
