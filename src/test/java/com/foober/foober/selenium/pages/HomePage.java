package com.foober.foober.selenium.pages;

import lombok.SneakyThrows;
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
    }

    @FindBy(css = "ul > li:nth-child(3)")
    private WebElement navbarItemText;

    public boolean pageLoadedCorrectly() {
        (new WebDriverWait(driver, 3)).until(ExpectedConditions.visibilityOf(navbarItemText));
        System.out.println(navbarItemText.getText());
        return !navbarItemText.getText().equals("Log in");
    }
}
