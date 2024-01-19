package fr.isep.projetseculogiciel;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CSRFController {

    private JsonArray securityFailures;
    private static final String FILENAME = "security_failures.json";

    public CSRFController(String url) {
        this.securityFailures = readJsonFromFile(FILENAME);
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            driver.get(url);
            List<WebElement> inputs = driver.findElements(By.tagName("input"));
            for (WebElement input : inputs) {
                checkForCsrfToken(driver, input, url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
            writeJsonToFile(securityFailures, FILENAME);
        }
    }

    private void checkForCsrfToken(WebDriver driver, WebElement input, String url) {
        try {
            boolean csrfTokenFound = false;
            String inputType = input.getAttribute("type");
            String inputName = input.getAttribute("name");

            if ("hidden".equalsIgnoreCase(inputType)) {
                if (inputName != null
                        && (inputName.toLowerCase().contains("csrf") || inputName.toLowerCase().contains("token"))) {
                    csrfTokenFound = true;
                }
            }

            if (!csrfTokenFound) {
                JsonObject failure = new JsonObject();
                failure.addProperty("security_failure_type", "CSRF");
                failure.addProperty("security_failure_location", url);
                failure.addProperty("security_failure_severity", "High");

                securityFailures.add(failure);
                System.out.println("Potential CSRF vulnerability detected - no token found in form.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JsonArray readJsonFromFile(String filename) {
        if (Files.exists(Paths.get(filename))) {
            try (JsonReader reader = new JsonReader(new FileReader(filename))) {
                return JsonParser.parseReader(reader).getAsJsonArray();
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
            System.out.println("Usage: java CSRFController <URL>");
            return;
        }
        new CSRFController(args[0]);
    }
}
