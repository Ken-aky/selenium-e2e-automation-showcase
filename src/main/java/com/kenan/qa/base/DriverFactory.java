package com.kenan.qa.base;

import com.kenan.qa.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

/**
 * Factory class responsible for initializing and managing the WebDriver lifecycle.
 * Utilizes ThreadLocal to support thread-safe parallel test execution.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverFactory() {
        // Prevent instantiation of factory class
    }

    /**
     * Initializes a new WebDriver instance based on configuration properties
     * and binds it to the current thread.
     *
     * @return Initialized WebDriver instance.
     */
    public static WebDriver initDriver() {
        String browser = ConfigReader.getBrowser();
        boolean isHeadless = ConfigReader.isHeadless();

        WebDriver driver;

        if ("chrome".equalsIgnoreCase(browser)) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();

            if (isHeadless) {
                options.addArguments("--headless=new");
                options.addArguments("--window-size=1920,1080");
                options.addArguments("--disable-gpu");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
            } else {
                options.addArguments("--start-maximized");
            }

            driver = new ChromeDriver(options);
        } else {
            throw new IllegalArgumentException("Nicht unterstützter Browser: " + browser + ". Bitte 'chrome' konfigurieren.");
        }

        // Deterministisches Waiting: Implizites Timeout strikt auf 0 setzen
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

        // Konsistente Viewport-Größe für Headless- und GUI-Modus sicherstellen
        try {
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
            if (!isHeadless) {
                driver.manage().window().maximize();
            }
        } catch (Exception ignored) {
        }

        driverThreadLocal.set(driver);
        return driver;
    }

    /**
     * Retrieves the WebDriver bound to the current thread.
     *
     * @return WebDriver for current execution thread.
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver wurde für diesen Thread noch nicht initialisiert. Bitte vorher initDriver() aufrufen.");
        }
        return driver;
    }

    /**
     * Safely closes and removes the WebDriver instance for the current thread.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
}
