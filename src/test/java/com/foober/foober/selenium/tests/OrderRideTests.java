package com.foober.foober.selenium.tests;


import com.foober.foober.selenium.helper.Helper;
import com.foober.foober.selenium.pages.HomePage;
import com.foober.foober.selenium.pages.LogInPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.testng.Assert.assertTrue;

public class OrderRideTests {

    private static WebDriver driver;

    public void initializeWebDriver() {
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        driver = new ChromeDriver();

        driver.manage().window().maximize();
    }

    private final String CLIENT_EMAIL = "client@gmail.com";
    private final String CLIENT_PASSWORD = "client";

    @BeforeEach
    public void loginClient() {
        initializeWebDriver();
        LogInPage logInPage = new LogInPage(driver);
        assertTrue(logInPage.confirmPageLoaded());
        logInPage.inputEmail(CLIENT_EMAIL);
        logInPage.inputPassword(CLIENT_PASSWORD);
        logInPage.clickOnLogIn();
    }

    @AfterEach
    public void quitDriver() {
        driver.quit();
    }

    @Test
    public void should_order_ride() {
        HomePage homePage = new HomePage(driver);
        homePage.enterPickupLocation("FTN");
        homePage.enterDestination("Srpsko narodno pozoriste");
        homePage.requestRide();
        assertTrue(homePage.payRideButtonAvailable());
        Helper.takeScreenshot(driver, "order_ride_test_order_ride");
        homePage.cancelRide();
    }

    @Test
    public void should_order_ride_with_stops() {
        HomePage homePage = new HomePage(driver);
        homePage.enterPickupLocation("Promenada");
        homePage.addStop();
        homePage.enterStop1("Liman");
        homePage.addStop();
        homePage.enterStop2("Kineska cetvrt");
        homePage.enterDestination("FTN");
        homePage.clickAllowBabiesBox();
        homePage.requestRide();
        assertTrue(homePage.payRideButtonAvailable());
        Helper.takeScreenshot(driver, "order_ride_test_order_ride_with_stops");
        homePage.cancelRide();
    }
}
