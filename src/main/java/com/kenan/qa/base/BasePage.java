package com.kenan.qa.base;

import com.kenan.qa.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * BasePage encapsulates common WebDriver operations and provides deterministic explicit waits.
 * Follows enterprise POM best practices: strict separation of concerns, zero assertions, and zero Thread.sleep.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public BasePage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver darf nicht null sein.");
        }
        this.driver = driver;
        this.wait = new WebDriverWait(driver, ConfigReader.getExplicitTimeoutDuration());
    }

    /**
     * Deterministischer Klick auf ein Element mit Warten auf Klickbarkeit.
     * Enthält einen resilienten Fallback via JavaScript, falls ein Element temporär überdeckt wird.
     */
    protected void click(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        try {
            element.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    /**
     * Führt einen Klick direkt über JavaScript im DOM aus – immun gegen Scrolling- und Viewport-Probleme.
     */
    protected void jsClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * Deterministische Texteingabe: Wartet auf Sichtbarkeit, leert das Feld und sendet Tasteneingaben.
     */
    protected void type(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        if (text != null) {
            element.sendKeys(text);
        }
    }

    /**
     * Liest den Text eines sichtbaren Elements aus.
     */
    protected String getText(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText().trim();
    }

    /**
     * Prüft deterministisch, ob ein Element sichtbar ist, ohne Exceptions bei Abwesenheit zu werfen.
     */
    protected boolean isDisplayed(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Liefert die aktuelle URL des Browsers.
     */
    public String getUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Wartet auf die Sichtbarkeit eines Elements und gibt es zurück.
     */
    protected WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Liefert eine Liste aller passenden sichtbaren Elemente.
     */
    protected List<WebElement> findElements(By locator) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        return driver.findElements(locator);
    }

    /**
     * Wählt eine Option in einem Select-Dropdown anhand des sichtbaren Textes aus.
     */
    protected void selectByVisibleText(By locator, String visibleText) {
        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        Select select = new Select(selectElement);
        select.selectByVisibleText(visibleText);
    }
}
