package com.foodapp;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FoodOrderSeleniumTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox",
                "--disable-dev-shm-usage", "--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    // Helper: fills the form and submits it
    private void placeOrder(String name, String item, String qty) {
        driver.get(baseUrl + "/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("customerName")));

        driver.findElement(By.id("customerName")).sendKeys(name);
        new Select(driver.findElement(By.id("item"))).selectByVisibleText(item);

        driver.findElement(By.id("quantity")).clear();
        driver.findElement(By.id("quantity")).sendKeys(qty);

        driver.findElement(By.id("placeOrderBtn")).click();

        // Wait until the confirmation page is shown
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        } catch (Exception e) {
            // Helps diagnose: shows what the browser actually displayed
            System.out.println("=== PAGE SOURCE AFTER SUBMIT ===");
            System.out.println(driver.getPageSource());
            throw e;
        }
    }

    @Test
    @DisplayName("TC1: Home page opens with correct title and heading")
    void homePageLoads() {
        driver.get(baseUrl + "/");
        assertEquals("Online Food Ordering System", driver.getTitle());
        assertEquals("Online Food Ordering System",
                driver.findElement(By.id("heading")).getText());
    }

    @Test
    @DisplayName("TC2: User can place an order successfully")
    void placeOrderSuccessfully() {
        placeOrder("Asgar", "Veg Burger", "2");

        assertEquals("Order placed successfully!",
                driver.findElement(By.id("message")).getText());
        assertTrue(driver.findElement(By.id("customer")).getText().contains("Asgar"));
    }

    @Test
    @DisplayName("TC3: Total amount is calculated correctly (3 x Masala Dosa = 300)")
    void totalIsCalculatedCorrectly() {
        placeOrder("Test User", "Masala Dosa", "3");

        assertEquals("Total Amount: Rs. 300",
                driver.findElement(By.id("total")).getText());
    }
}