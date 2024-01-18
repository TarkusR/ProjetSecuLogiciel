package fr.isep.projetseculogiciel;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;

import java.util.List;

public class CSRFController {

    private static JSONArray securityFailures = new JSONArray();

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

                JSONObject failure = new JSONObject();
                failure.put("security_failure_type", "CSRF");
                failure.put("security_failure_location", form.getAttribute("action"));
                failure.put("security_failure_severity", "High"); // Example severity

                securityFailures.put(failure);
                System.out.println("Potential CSRF vulnerability detected - no token found in form.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeJsonToFile(JSONArray data, String filename) {
        try (FileWriter file = new FileWriter(filename)) {
            file.write(data.toString(4)); // Indentation for readability
            System.out.println("Successfully written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
