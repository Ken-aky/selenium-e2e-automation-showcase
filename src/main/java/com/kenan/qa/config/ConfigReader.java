package com.kenan.qa.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

/**
 * Thread-safe configuration manager that loads test runtime configurations
 * from config.properties with optional system property override support.
 */
public final class ConfigReader {

    private static final String DEFAULT_CONFIG_FILE = "config.properties";
    private static final Properties properties = new Properties();

    static {
        loadProperties(DEFAULT_CONFIG_FILE);
    }

    private ConfigReader() {
        // Prevent instantiation of utility class
    }

    private static void loadProperties(String configFileName) {
        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(configFileName)) {
            if (inputStream == null) {
                throw new IllegalStateException("Konfigurationsdatei '" + configFileName + "' wurde nicht im Classpath gefunden.");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Laden der Konfigurationsdatei: " + configFileName, e);
        }
    }

    /**
     * Ermittelt einen Property-Wert mit Vorrang für System-Properties (CLI-Overrides via -Dkey=value).
     */
    public static String getProperty(String key) {
        String systemProperty = System.getProperty(key);
        if (systemProperty != null && !systemProperty.isBlank()) {
            return systemProperty.trim();
        }
        String value = properties.getProperty(key);
        return value != null ? value.trim() : null;
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    public static String getBaseUrl() {
        String baseUrl = getProperty("base.url");
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("Property 'base.url' ist nicht konfiguriert.");
        }
        return baseUrl;
    }

    public static String getBrowser() {
        return getProperty("browser", "chrome").toLowerCase();
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }

    public static int getExplicitTimeout() {
        try {
            return Integer.parseInt(getProperty("timeout.explicit", "10"));
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    public static Duration getExplicitTimeoutDuration() {
        return Duration.ofSeconds(getExplicitTimeout());
    }
}
