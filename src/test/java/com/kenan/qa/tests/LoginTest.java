package com.kenan.qa.tests;

import com.kenan.qa.base.BaseTest;
import com.kenan.qa.pages.InventoryPage;
import com.kenan.qa.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class covering positive and negative authentication flows on SauceDemo.
 */
public class LoginTest extends BaseTest {

    @Test(description = "Validiert den erfolgreichen Login mit Standard-Benutzerdaten und Weiterleitung zum Produktkatalog")
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = loginPage.loginAs("standard_user", "secret_sauce");

        Assert.assertTrue(inventoryPage.getUrl().contains("inventory.html"),
                "Die Ziel-URL sollte 'inventory.html' enthalten.");
    }

    @DataProvider(name = "negativeLoginData")
    public Object[][] getNegativeLoginData() {
        return new Object[][]{
                {"locked_out_user", "secret_sauce", "Epic sadface: Sorry, this user has been locked out."},
                {"standard_user", "invalid_password", "Username and password do not match"},
                {"", "secret_sauce", "Epic sadface: Username is required"},
                {"standard_user", "", "Epic sadface: Password is required"}
        };
    }

    @Test(dataProvider = "negativeLoginData", description = "Validiert Fehlermeldungen bei ungültigen, gesperrten oder leeren Zugangsdaten")
    public void testNegativeLoginScenarios(String username, String password, String expectedErrorMessage) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Fehlermeldung sollte auf der Login-Seite angezeigt werden.");
        Assert.assertTrue(loginPage.getErrorMessage().contains(expectedErrorMessage),
                "Erwarteter Text '" + expectedErrorMessage + "' nicht in Fehlermeldung: '" + loginPage.getErrorMessage() + "' enthalten.");
    }
}
