package com.kenan.qa.pages;

import com.kenan.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object representing Step Two of Checkout (Order Overview & Price Calculation).
 */
public class CheckoutStepTwoPage extends BasePage {

    private final By itemTotalLabel = By.className("summary_subtotal_label");
    private final By taxLabel = By.className("summary_tax_label");
    private final By totalLabel = By.className("summary_total_label");
    private final By finishButton = By.id("finish");

    public CheckoutStepTwoPage(WebDriver driver) {
        super(driver);
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("checkout-step-two.html"));
        waitForVisibility(finishButton);
    }

    private double extractPrice(By locator) {
        String rawText = getText(locator);
        // Extrahiert den numerischen Betrag (z. B. aus 'Item total: $29.99' -> 29.99)
        String numericText = rawText.replaceAll("[^0-9.]", "").trim();
        return Double.parseDouble(numericText);
    }

    /**
     * Liest die Zwischensumme (Item total) aus.
     */
    public double getItemTotal() {
        return extractPrice(itemTotalLabel);
    }

    /**
     * Liest die Steuer (Tax) aus.
     */
    public double getTax() {
        return extractPrice(taxLabel);
    }

    /**
     * Liest den Gesamtbetrag (Total) aus.
     */
    public double getTotal() {
        return extractPrice(totalLabel);
    }

    /**
     * Schließt die Bestellung ab und navigiert zur Bestätigungsseite.
     */
    public CheckoutCompletePage clickFinish() {
        jsClick(finishButton);
        return new CheckoutCompletePage(driver);
    }
}
