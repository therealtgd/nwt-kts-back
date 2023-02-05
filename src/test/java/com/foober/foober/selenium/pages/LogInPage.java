package com.foober.foober.selenium.pages;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LogInPage {
    private WebDriver driver;
    private static String PAGE_URL="http://localhost:4200/login";
    @FindBy(xpath = "//*[@id=\"logInCard\"]/div/div/div[1]")
    private WebElement logInCard;
    @FindBy(css = "#emailInput")
    private WebElement emailField;
    @FindBy(xpath = "//*[@id=\"passwordInput\"]/div/input")
    private WebElement passwordField;
    @FindBy(css = "#logInBtn")
    private WebElement logInButton;
    @FindBy(xpath = "//*[@id=\"okBtn\"]/button")
    private WebElement okButton;
    public LogInPage (WebDriver driver){
        this.driver=driver;
        driver.get(PAGE_URL);
        PageFactory.initElements(driver, this);
    }

    public boolean confirmPageLoaded() {
        (new WebDriverWait(driver, 3)).until(ExpectedConditions.visibilityOf(logInCard));
        return true;
    }

    public void inputEmail(String validEmail) {
        emailField.click();
        emailField.sendKeys(validEmail);
    }

    public void inputPassword(String validPassword) {
        passwordField.click();
        passwordField.sendKeys(validPassword);
    }

    public void clickOnLogIn() {
        logInButton.click();
    }

    public boolean clickOnOk() {
        (new WebDriverWait(driver, 3)).until(ExpectedConditions.visibilityOf(okButton));
        okButton.click();
        return true;
    }
}
