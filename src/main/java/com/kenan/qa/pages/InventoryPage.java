package com.kenan.qa.pages;

import com.kenan.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object representing the SauceDemo Inventory/Product Catalog Page.
 */
public class InventoryPage extends BasePage {

    private final By inventoryList = By.className("inventory_list");
    private final By shoppingCartBadge = By.className("shopping_cart_badge");
    private final By shoppingCartLink = By.className("shopping_cart_link");
    private final By sortDropdown = By.className("product_sort_container");
    private final By itemPrices = By.className("inventory_item_price");

    public InventoryPage(WebDriver driver) {
        super(driver);
        // Sicherstellen, dass die Inventarliste sichtbar geladen ist
        waitForVisibility(inventoryList);
    }

    /**
     * Fügt ein Produkt anhand seines Namens in den Warenkorb hinzu.
     */
    public InventoryPage addProductToCart(String productName) {
        String buttonId = "add-to-cart-" + productName.toLowerCase().replace(" ", "-");
        jsClick(By.id(buttonId));
        return this;
    }

    /**
     * Ermittelt die Anzahl der Artikel im Warenkorb anhand des Badges.
     * Gibt 0 zurück, falls aktuell kein Badge angezeigt wird.
     */
    public int getCartBadgeCount() {
        if (!isDisplayed(shoppingCartBadge)) {
            return 0;
        }
        String countText = getText(shoppingCartBadge);
        try {
            return Integer.parseInt(countText);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Wählt eine Sortieroption im Dropdown über den sichtbaren Text aus
     * (z. B. "Price (low to high)").
     */
    public InventoryPage selectSortOption(String visibleText) {
        selectByVisibleText(sortDropdown, visibleText);
        return this;
    }

    /**
     * Liest alle Produktpreise aus dem DOM aus und konvertiert sie in eine List<Double>.
     */
    public List<Double> getAllProductPrices() {
        List<WebElement> priceElements = findElements(itemPrices);
        List<Double> prices = new ArrayList<>();
        for (WebElement element : priceElements) {
            String text = element.getText().replace("$", "").trim();
            prices.add(Double.parseDouble(text));
        }
        return prices;
    }

    /**
     * Klickt auf den Warenkorb und navigiert zur CartPage.
     */
    public CartPage clickCart() {
        jsClick(shoppingCartLink);
        return new CartPage(driver);
    }
}
