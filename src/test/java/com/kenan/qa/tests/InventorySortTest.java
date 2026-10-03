package com.kenan.qa.tests;

import com.kenan.qa.base.BaseTest;
import com.kenan.qa.pages.InventoryPage;
import com.kenan.qa.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Test class verifying sorting functionality in the product catalog.
 */
public class InventorySortTest extends BaseTest {

    @Test(description = "Validiert die Preissortierung 'Price (low to high)' auf mathematische Korrektheit")
    public void testPriceSortLowToHigh() {
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = loginPage.loginAs("standard_user", "secret_sauce");

        inventoryPage.selectSortOption("Price (low to high)");

        List<Double> actualPrices = inventoryPage.getAllProductPrices();
        Assert.assertFalse(actualPrices.isEmpty(), "Die Preisliste darf nicht leer sein.");

        List<Double> expectedSortedPrices = new ArrayList<>(actualPrices);
        Collections.sort(expectedSortedPrices);

        Assert.assertEquals(actualPrices, expectedSortedPrices,
                "Die Produktpreise auf der Seite sind nicht wie erwartet aufsteigend (low to high) sortiert.");
    }
}
