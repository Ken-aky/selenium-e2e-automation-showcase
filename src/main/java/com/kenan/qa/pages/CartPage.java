package com.kenan.qa.pages;

import com.kenan.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object representing the SauceDemo Shopping Cart Page.
 */
public class CartPage extends BasePage {

    private final By cartItems = By.className("cart_item");
    private final By itemNames = By.className("inventory_item_name");
    private final By checkoutButton = By.id("checkout");

    public CartPage(WebDriver driver) {
        super(driver);
        waitForVisibility(checkoutButton);
    }

    /**
     * Liest alle Produktnamen aus, die aktuell im Warenkorb liegen.
     */
    public List<String> getItemNames() {
        List<WebElement> nameElements = driver.findElements(itemNames);
        List<String> names = new ArrayList<>();
        for (WebElement element : nameElements) {
            names.add(element.getText().trim());
        }
        return names;
    }

    /**
     * Prüft, ob ein Produkt mit dem angegebenen Namen im Warenkorb vorhanden ist.
     */
    public boolean isItemInCart(String productName) {
        return getItemNames().contains(productName);
    }

    /**
     * Klickt auf den Checkout-Button und führt zum ersten Schritt des Bestellprozesses.
     */
    public CheckoutStepOnePage clickCheckout() {
        jsClick(checkoutButton);
        return new CheckoutStepOnePage(driver);
    }
}
