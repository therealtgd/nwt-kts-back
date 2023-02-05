package com.foober.foober.selenium.pages;

import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Objects;

public class HomePage {
    private WebDriver driver;
    public HomePage (WebDriver driver){
        this.driver=driver;
        PageFactory.initElements(driver, this);
        reload();
    }

    @FindBy(css = "ul > li.p-hidden")
    private WebElement navbarItemText;
    @FindBy(id="pickupLocation")
    private WebElement pickupLocation;
    @FindBy(id="stop1")
    private WebElement stop1;
    @FindBy(id="stop2")
    private WebElement stop2;
    @FindBy(id="destinationLocation")
    private WebElement destination;
    @FindBy(id="babiesAllowedBox")
    private WebElement babiesAllowedBox;
    @FindBy(id="requestRideButton")
    private WebElement requestRideButton;
    @FindBy(css=".pac-item")
    private WebElement locationResult;
    @FindBy(id="addStopButton")
    private WebElement addStopButton;
    @FindBy(id="payRideButton")
    private WebElement payRideButton;
    @FindBy(css = "button.p-dialog-header-close")
    private WebElement cancelRideButton;

    private void reload() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        driver.navigate().refresh();
    }

    public boolean pageLoadedCorrectly() {
        (new WebDriverWait(driver, 3)).until(ExpectedConditions.invisibilityOf(navbarItemText));
        return true;
    }

    private void clickLocation() {
        (new WebDriverWait(driver, 3)).until(ExpectedConditions.visibilityOf(locationResult));
        locationResult.click();
    }

    public void enterPickupLocation(String location) {
        pickupLocation.sendKeys(location);
        clickLocation();
    }

    public void enterDestination(String location) {
        destination.sendKeys(location);
        clickLocation();
    }

    public void enterStop1(String location) {
        (new WebDriverWait(driver, 3)).until(ExpectedConditions.presenceOfElementLocated(By.id("stop1")));
        stop1.sendKeys(location);
        clickLocation();
    }

    public void enterStop2(String location) {
        (new WebDriverWait(driver, 3)).until(ExpectedConditions.presenceOfElementLocated(By.id("stop2")));
        stop2.sendKeys(location);
        clickLocation();
    }

    public void clickAllowBabiesBox() {
        babiesAllowedBox.click();
    }

    public void requestRide() {
        (new WebDriverWait(driver, 3)).until(ExpectedConditions.elementToBeClickable(requestRideButton));
        requestRideButton.click();
    }

    public boolean payRideButtonAvailable() {
        (new WebDriverWait(driver, 3)).until(ExpectedConditions.presenceOfElementLocated(By.id("payRideButton")));
        return true;
    }

    public void cancelRide() {
        (new WebDriverWait(driver, 3))
            .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button.p-dialog-header-close")));
        cancelRideButton.click();
        (new WebDriverWait(driver, 3))
            .until(ExpectedConditions.attributeToBe(By.cssSelector("app-modal"), "ng-reflect-visible", "false"));
    }

    public void addStop() {
        addStopButton.click();
    }
}
