package com.kenan.qa.pages;

import com.kenan.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object representing the Checkout Complete / Order Confirmation Page.
 */
public class CheckoutCompletePage extends BasePage {

    private final By completeHeader = By.className("complete-header");

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
        waitForVisibility(completeHeader);
    }

    /**
     * Liest die Erfolgsnachricht ("Thank you for your order!") aus.
     */
    public String getSuccessMessage() {
        return getText(completeHeader);
    }
}
