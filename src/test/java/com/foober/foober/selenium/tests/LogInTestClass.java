package com.foober.foober.selenium.tests;

import com.foober.foober.selenium.pages.HomePage;
import com.foober.foober.selenium.pages.LogInPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class LogInTestClass {
    public static WebDriver driver;

    private static final String VALID_EMAIL = "admin@gmail.com";
    private static final String VALID_PASSWORD = "admin";
    private static final String INVALID_EMAIL = "123@gmail.com";
    private static final String INVALID_PASSWORD = "123";

    @BeforeSuite
    public void initializeWebDriver() {
        System.setProperty("webdriver.edge.driver", "msedgedriver.exe");
        driver = new EdgeDriver();

        driver.manage().window().maximize();
    }

    @AfterSuite
    public void quitDriver() {
        driver.quit();
    }

    @Test
    public void testSuccessfulLogIn() {
        LogInPage logInPage = new LogInPage(driver);
        assertTrue(logInPage.confirmPageLoaded());
        logInPage.inputEmail(VALID_EMAIL);
        logInPage.inputPassword(VALID_PASSWORD);
        logInPage.clickOnLogIn();

        HomePage homePage = new HomePage(driver);
        assertTrue(homePage.pageLoadedCorrectly());
    }
    @Test
    public void testInvalidEmailLogIn() {
        LogInPage logInPage = new LogInPage(driver);
        assertTrue(logInPage.confirmPageLoaded());
        logInPage.inputEmail(INVALID_EMAIL);
        logInPage.inputPassword(VALID_PASSWORD);
        logInPage.clickOnLogIn();
        assertTrue(logInPage.clickOnOk());
    }
    @Test
    public void testInvalidPasswordLogIn() {
        LogInPage logInPage = new LogInPage(driver);
        assertTrue(logInPage.confirmPageLoaded());
        logInPage.inputEmail(VALID_EMAIL);
        logInPage.inputPassword(INVALID_PASSWORD);
        logInPage.clickOnLogIn();
        assertTrue(logInPage.clickOnOk());
    }
    @Test
    public void testEmptyFormLogIn() {
        LogInPage logInPage = new LogInPage(driver);
        assertTrue(logInPage.confirmPageLoaded());
        logInPage.clickOnLogIn();
        assertTrue(logInPage.clickOnOk());
    }
}
