package fr.isep.projetseculogiciel;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class BrokenAccessControl {

    private static String initialTitle = "";

    public static void main(String[] args) {
        // Set up WebDriver
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            // Open the desired website
            if (args.length != 1) {
                System.out.println("Enter a parameter");
                return;
            }

            String baseUrl = args[0];

            // Access the first URL and store its title
            initialTitle = accessUrl(driver, baseUrl);

            // Access different URLs
            accessUrl(driver, baseUrl + "/ftp");
            accessUrl(driver, baseUrl + "/files");
            accessUrl(driver, baseUrl + "/file");
            accessUrl(driver, baseUrl + "/product");

        } finally {
            // Close the browser window
            driver.quit();
        }
    }

    private static String accessUrl(WebDriver driver, String url) {
        driver.get(url);

        // Get the current page title
        String currentTitle = driver.getTitle();

        // Check if the page title has changed compared to the initial title
        if (!currentTitle.equals(initialTitle)) {
            System.out.println("Navigated to: " + url);
            // You can add further interactions or assertions here
        } else {
            System.out.println("No change on the page: " + url);
        }

        // Return the current title for the first access
        return currentTitle;
    }
}

