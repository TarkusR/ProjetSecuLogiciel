package fr.isep.projetseculogiciel;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSRFController {

    private static JsonArray securityFailures = new JsonArray();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java CSRFController <URL>");
            return;
        }
        String url = args[0];
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            driver.get(url);
            List<WebElement> forms = driver.findElements(By.tagName("form"));
            for (WebElement form : forms) {
                checkForCsrfToken(driver, form);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        writeJsonToFile(securityFailures, "security_failures.json");

    }

    private static void checkForCsrfToken(WebDriver driver, WebElement form) {
        try {
            // Find all hidden input elements within the form
            List<WebElement> hiddenInputs = form.findElements(By.cssSelector("input[type='hidden']"));

            boolean csrfTokenFound = false;
            for (WebElement input : hiddenInputs) {
                String inputName = input.getAttribute("name");
                // Simple heuristic: check if the input name contains common CSRF token names
                if (inputName != null
                        && (inputName.toLowerCase().contains("csrf") || inputName.toLowerCase().contains("token"))) {
                    csrfTokenFound = true;
                    break;
                }
            }

            if (csrfTokenFound) {
                System.out.println("CSRF token found in form.");
            } else {
                JsonObject failure = new JsonObject();
                failure.addProperty("security_failure_type", "CSRF");
                failure.addProperty("security_failure_location", form.getAttribute("action"));
                failure.addProperty("security_failure_severity", "High"); // Example severity

                securityFailures.add(failure);
                System.out.println("Potential CSRF vulnerability detected - no token found in form.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeJsonToFile(JsonArray data, String filename) {
        try (FileWriter file = new FileWriter(filename)) {
            Gson gson = new Gson();
            String json = gson.toJson(data);
            file.write(json); // Write JSON string
            System.out.println("Successfully written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
