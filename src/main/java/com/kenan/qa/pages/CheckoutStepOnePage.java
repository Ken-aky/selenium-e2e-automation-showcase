package com.kenan.qa.pages;

import com.kenan.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object representing Step One of Checkout (User Information Form).
 */
public class CheckoutStepOnePage extends BasePage {

    private final By firstNameInput = By.id("first-name");
    private final By lastNameInput = By.id("last-name");
    private final By postalCodeInput = By.id("postal-code");
    private final By continueButton = By.id("continue");

    public CheckoutStepOnePage(WebDriver driver) {
        super(driver);
        waitForVisibility(firstNameInput);
    }

    public CheckoutStepOnePage enterFirstName(String firstName) {
        type(firstNameInput, firstName);
        return this;
    }

    public CheckoutStepOnePage enterLastName(String lastName) {
        type(lastNameInput, lastName);
        return this;
    }

    public CheckoutStepOnePage enterPostalCode(String postalCode) {
        type(postalCodeInput, postalCode);
        return this;
    }

    public void clickContinue() {
        WebElement btn = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(continueButton));
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);
            btn.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }

        // Falls die URL nicht sofort wechselt, Klick via JavaScript als Absicherung auslösen
        if (!driver.getCurrentUrl().contains("checkout-step-two.html")) {
            try {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Füllt die Kundendaten vollständig aus, klickt auf 'Continue' und wartet deterministisch auf die Folgeseite.
     */
    public CheckoutStepTwoPage fillInformationAndContinue(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
        clickContinue();
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("checkout-step-two.html"));
        return new CheckoutStepTwoPage(driver);
    }
}
