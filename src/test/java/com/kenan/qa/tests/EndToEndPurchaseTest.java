package com.kenan.qa.tests;

import com.kenan.qa.base.BaseTest;
import com.kenan.qa.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * End-to-End Test covering the complete purchase journey on SauceDemo.
 */
public class EndToEndPurchaseTest extends BaseTest {

    @Test(description = "Durchläuft den vollständigen Checkout Happy-Path: Login, Produktauswahl, Warenkorb, Bestelldaten und Abschluss")
    public void testCompletePurchaseFlow() {
        String product1 = "Sauce Labs Backpack";
        String product2 = "Sauce Labs Bike Light";
        double priceProduct1 = 29.99;
        double priceProduct2 = 9.99;
        double expectedItemTotal = priceProduct1 + priceProduct2;

        // 1. Login
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = loginPage.loginAs("standard_user", "secret_sauce");

        // 2. Produkte zum Warenkorb hinzufügen
        inventoryPage.addProductToCart(product1)
                     .addProductToCart(product2);

        // 3. Cart-Badge verifizieren
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), 2,
                "Der Warenkorb-Badge sollte genau 2 Artikel anzeigen.");

        // 4. Zum Warenkorb navigieren & Artikel prüfen
        CartPage cartPage = inventoryPage.clickCart();
        List<String> itemsInCart = cartPage.getItemNames();
        Assert.assertTrue(itemsInCart.contains(product1), "Produkt '" + product1 + "' sollte im Warenkorb liegen.");
        Assert.assertTrue(itemsInCart.contains(product2), "Produkt '" + product2 + "' sollte im Warenkorb liegen.");

        // 5. Checkout Step One: Kundendaten ausfüllen
        CheckoutStepOnePage stepOnePage = cartPage.clickCheckout();
        CheckoutStepTwoPage stepTwoPage = stepOnePage.fillInformationAndContinue("Max", "Mustermann", "10115");

        // 6. Checkout Step Two: Mathematische Preisvalidierung
        double actualItemTotal = stepTwoPage.getItemTotal();
        double actualTax = stepTwoPage.getTax();
        double actualTotal = stepTwoPage.getTotal();

        Assert.assertEquals(actualItemTotal, expectedItemTotal, 0.001,
                "Die berechnete Zwischensumme stimmt nicht mit der Summe der Einzelpreise überein.");
        Assert.assertEquals(Math.round((actualItemTotal + actualTax) * 100.0) / 100.0, actualTotal, 0.001,
                "Gesamtsumme (Total) entspricht nicht der Summe aus Item Total und Tax.");

        // 7. Finish & Abschluss-Verifikation
        CheckoutCompletePage completePage = stepTwoPage.clickFinish();
        Assert.assertEquals(completePage.getSuccessMessage(), "Thank you for your order!",
                "Die Erfolgsnachricht der Bestellung weicht vom erwarteten Text ab.");
    }
}
