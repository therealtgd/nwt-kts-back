package com.foober.foober.selenium.tests;

import com.foober.foober.selenium.helper.Helper;
import com.foober.foober.selenium.pages.HomePage;
import com.foober.foober.selenium.pages.LogInPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RideEndTests {

    private static WebDriver driverClient;
    private static WebDriver driverDriver;


    public void initializeWebDrivers() {
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        driverClient = new ChromeDriver();
        driverClient.manage().window().maximize();

        driverDriver = new ChromeDriver();
        driverDriver.manage().window().maximize();
    }

    private final String CLIENT_EMAIL = "client@gmail.com";
    private final String CLIENT_PASSWORD = "client";
    private final String DRIVER_EMAIL = "driver3@gmail.com";
    private final String DRIVER_PASSWORD = "driver";

    @BeforeEach
    public void loginClient() {
        initializeWebDrivers();
        LogInPage logInPage = new LogInPage(driverClient);
        assertTrue(logInPage.confirmPageLoaded());
        logInPage.inputEmail(CLIENT_EMAIL);
        logInPage.inputPassword(CLIENT_PASSWORD);
        logInPage.clickOnLogIn();

        logInPage = new LogInPage(driverDriver);
        assertTrue(logInPage.confirmPageLoaded());
        logInPage.inputEmail(DRIVER_EMAIL);
        logInPage.inputPassword(DRIVER_PASSWORD);
        logInPage.clickOnLogIn();
    }

    @AfterEach
    public void quitDriver() {
        driverClient.quit();
        driverDriver.quit();
    }

    @Test
    public void should_end_ride() {
        HomePage homeClient = new HomePage(driverClient);
        HomePage homeDriver = new HomePage(driverDriver);

        homeClient.enterPickupLocation("FTN");
        homeClient.enterDestination("Studentska menza");
        homeClient.requestRide();
        assertTrue(homeClient.payRideButtonAvailable());
        homeClient.payRide();

        homeDriver.startRide();
        assertTrue(homeDriver.finishRideAvailable());
        Helper.takeScreenshot(driverClient, "end_ride");
        homeDriver.finishRide();
    }
}
