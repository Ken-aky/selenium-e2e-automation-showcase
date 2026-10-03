package com.kenan.qa.pages;

import com.kenan.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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
        click(continueButton);
    }

    /**
     * Füllt die Kundendaten vollständig aus und klickt auf 'Continue'.
     */
    public CheckoutStepTwoPage fillInformationAndContinue(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
        clickContinue();
        return new CheckoutStepTwoPage(driver);
    }
}
