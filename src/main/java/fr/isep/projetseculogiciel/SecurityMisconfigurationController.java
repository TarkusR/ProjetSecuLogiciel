package fr.isep.projetseculogiciel;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class SecurityMisconfigurationController {

    private static final List<String> SECURITY_HEADERS_TO_CHECK = List.of(
            "X-Frame-Options",
            "X-Content-Type-Options",
            "X-XSS-Protection",
            "Content-Security-Policy",
            "Strict-Transport-Security"
    );

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java SecurityMisconfigurationScanner <URL>");
            return;
        }

        String url = args[0];
        scanForMisconfigurations(url);
    }

    private static void scanForMisconfigurations(String url) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            driver.get(url);
            System.out.println("Scanning URL: " + url);

            // Checking for missing security headers
            for (String header : SECURITY_HEADERS_TO_CHECK) {
                WebElement headerElement = driver.findElement(By.name(header));
                if (headerElement == null) {
                    System.out.println("Missing security header: " + header);
                }
            }

            // Check for Exposed Server Version
            WebElement serverHeader = driver.findElement(By.name("Server"));
            if (serverHeader != null) {
                System.out.println("Server version exposed: " + serverHeader.getText());
            }

            // Check for Default Credentials
            checkForDefaultCredentials(driver, url);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    private static void checkForDefaultCredentials(WebDriver driver, String url) {
        driver.get(url + "/login"); // Modify URL based on actual login endpoint

        WebElement usernameField = driver.findElement(By.name("username"));
        WebElement passwordField = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.id("login-button")); // Replace with actual login button identifier

        if (usernameField != null && passwordField != null && loginButton != null) {
            usernameField.sendKeys("admin");
            passwordField.sendKeys("admin123");
            loginButton.click();

            // Assuming successful login will redirect to a different page or have a different element
            WebElement loggedInElement = driver.findElement(By.id("logged-in-element")); // Replace with actual logged-in element identifier

            if (loggedInElement != null) {
                System.out.println("Default credentials are working: admin/admin123");
            }
        }
    }
}