# Enterprise Selenium WebDriver E2E Automation Framework

[![E2E Automation Tests](https://github.com/Ken-aky/selenium-e2e-automation-showcase/actions/workflows/e2e-tests.yml/badge.svg)](https://github.com/Ken-aky/selenium-e2e-automation-showcase/actions/workflows/e2e-tests.yml)
![Java](https://img.shields.io/badge/Java-17%20LTS-orange?logo=openjdk)
![Selenium](https://img.shields.io/badge/Selenium%20WebDriver-4.x-brightgreen?logo=selenium)
![TestNG](https://img.shields.io/badge/TestNG-7.x-blue)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven)
![License](https://img.shields.io/badge/License-MIT-green)

---

## 1. Executive Summary & Architektur-Ziele
Dieses Repository demonstriert ein robustes, skalierbares und wartbares End-to-End Testautomatisierungs-Framework auf Enterprise-Niveau für die Referenz-E-Commerce-Plattform [SauceDemo](https://www.saucedemo.com/).

Ziel des Projekts ist es, Best Practices moderner Testautomatisierung greifbar zu machen:

* **Deterministisches Waiting:** Vollständige Eliminierung von Test-Flakiness durch striktes Verbot von `Thread.sleep()` und standardmäßiges implizites Timeout = 0. Synchronisation erfolgt rein über explizite `WebDriverWait`-Bedingungen (`ExpectedConditions`).
* **Page Object Model (POM):** Klare Trennung von Verantwortlichkeiten. Page Objects kapseln ausschließlich Web-Elemente und Interaktionen – Assertions gehören exklusiv in den Test-Layer.
* **Thread-Safety & Parallelisierung:** Kapselung des WebDriver-Lifecycles in `ThreadLocal<WebDriver>` zur sicheren parallelen Ausführung ohne Race Conditions.
* **CI/CD-Readiness:** Vollautomatische Headless-Ausführung via GitHub Actions Pipeline mit dynamischen CLI-Overrides.

---

## 2. Architektur-Highlights & Design Patterns

### 🏛️ Page Object Model (POM) mit Fluent Interface
Die Seitenklassen erweitern die zentrale `BasePage`. Interaktionsmethoden liefern entweder `this` oder die Folge-Page-Instanz zurück. Das ermöglicht lesbare, kaskadierende Aufrufe ("Method Chaining") im Test-Layer:

```java
inventoryPage.addProductToCart("Sauce Labs Backpack")
             .addProductToCart("Sauce Labs Bike Light");
```

### 🔒 ThreadLocal Driver Lifecycle Management
In `DriverFactory` wird jede Browser-Instanz an den aktuellen Thread gebunden. `BaseTest` initialisiert den Treiber vor jedem Test neu (`@BeforeMethod`) und beendet ihn deterministisch (`@AfterMethod`), wodurch eine 100%ige Test-Isolation gewährleistet wird:

```java
public final class DriverFactory {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    // ...
}
```

### ⏱️ Deterministisches Warten (No Flakiness)
* Implizite Timeouts sind standardmäßig deaktiviert (`implicitlyWait(Duration.ofSeconds(0))`).
* Alle Klick-, Tipp- und Leseaktionen synchronisieren sich deterministisch über `ExpectedConditions.elementToBeClickable` und `ExpectedConditions.visibilityOfElementLocated`.
* Keine arbiträren Pausen (`Thread.sleep()`), was Ausführungszeiten minimiert und Instabilitäten verhindert.

### ⚙️ Flexible Konfiguration mit CLI-Overrides
Konfigurationen werden aus `src/main/resources/config.properties` geladen. Zur Laufzeit in CI/CD-Pipelines können alle Parameter über System-Properties überschrieben werden (z. B. `-Dheadless=true` oder `-Dbase.url=...`).

---

## 3. Test-Szenarien & Testabdeckung

| Testklasse | Typ | Beschreibung |
| :--- | :--- | :--- |
| `EndToEndPurchaseTest` | E2E Happy Path | Login als `standard_user`, Hinzufügen mehrerer Artikel, Validierung des Cart-Badges, Warenkorb-Prüfung, Ausfüllen der Checkout-Daten, mathematische Validierung von Zwischensumme (Item total) und Gesamtsumme (Total = Item total + Tax) sowie Verifikation der Abschlussmeldung ("Thank you for your order!"). |
| `LoginTest` | Data-Driven & Edge Cases | Positiver Login mit Weiterleitungsvalidierung (`inventory.html`) sowie datengetriebene Tests via TestNG `@DataProvider` für: gesperrten Nutzer (`locked_out_user`), falsches Passwort und leere Eingabefelder. |
| `InventorySortTest` | Logik-Validierung | Sortierung nach "Price (low to high)" im Dropdown. Alle Preise werden aus dem DOM als `List<Double>` extrahiert und mathematisch gegen eine aufsteigend sortierte Kopie validiert. |

---

## 4. Projektstruktur

```text
selenium-e2e-automation-showcase/
├── .github/
│   └── workflows/
│       └── e2e-tests.yml        # CI Pipeline (Ubuntu, Java 17, Headless Chrome)
├── pom.xml                      # Maven Build & Dependencies (Selenium 4, TestNG, Surefire)
├── testng.xml                   # TestNG Suite Definition
├── README.md                    # Projektdokumentation
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/kenan/qa/
    │   │       ├── base/
    │   │       │   ├── BasePage.java             # Explizite Waits, Klick-, Tipp- & Auslesemethoden
    │   │       │   └── DriverFactory.java        # ThreadLocal WebDriver, ChromeOptions, Headless
    │   │       ├── config/
    │   │       │   └── ConfigReader.java         # Properties-Loader mit CLI-Override-Support
    │   │       └── pages/
    │   │           ├── LoginPage.java            # Login-Page Object
    │   │           ├── InventoryPage.java        # Produktkatalog, Sortierung, Badge-Counter
    │   │           ├── CartPage.java             # Warenkorb-Ansicht & Validierung
    │   │           ├── CheckoutStepOnePage.java  # Formular Kundendaten
    │   │           ├── CheckoutStepTwoPage.java  # Bestellübersicht & Preisberechnung
    │   │           └── CheckoutCompletePage.java # Bestellbestätigung
    │   └── resources/
    │       └── config.properties                 # URL, Browser, Timeouts & Flags
    └── test/
        └── java/
            └── com/kenan/qa/
                ├── base/
                │   └── BaseTest.java             # Setup (@BeforeMethod) & Teardown (@AfterMethod)
                └── tests/
                    ├── LoginTest.java            # Positive & Data-Driven Login Tests
                    ├── EndToEndPurchaseTest.java # Kompletter Checkout Happy-Path
                    └── InventorySortTest.java    # Mathematische Preis-Sortierungsvalidierung
```

---

## 5. Ausführung & Befehle (How to Run)

### Voraussetzungen
* Java 17+ (JDK)
* Apache Maven 3.8+
* Google Chrome Browser

### Lokale Ausführung im UI-Modus (Browser sichtbar)
```bash
mvn clean test
```

### Ausführung im Headless-Modus (z. B. für Terminal & CI/CD)
```bash
mvn clean test -Dheadless=true
```

### Ausführung einer spezifischen Testklasse
```bash
# Nur den E2E Kaufprozess ausführen
mvn test -Dtest=EndToEndPurchaseTest

# Nur Login-Tests ausführen
mvn test -Dtest=LoginTest
```

### Ausführung über die TestNG Suite (`testng.xml`)
```bash
mvn test -Dsurefire.suiteXmlFiles=testng.xml
```

---

## 6. Reporting & CI/CD

### Testberichte
Nach jedem Testlauf generiert das Maven Surefire Plugin detaillierte HTML- und XML-Reports unter:
* `target/surefire-reports/index.html` (TestNG HTML Report)
* `target/surefire-reports/emailable-report.html` (Kompakter E-Mail Report)
* `target/surefire-reports/testng-results.xml` (Maschinenlesbare Ergebnisse)

### Continuous Integration (GitHub Actions)
Bei jedem `push` oder `pull_request` auf den `main`-Branch führt GitHub Actions den Workflow `.github/workflows/e2e-tests.yml` auf einem `ubuntu-latest` Runner aus. Die erzeugten Testberichte werden automatisch als Build-Artefakt (`surefire-test-reports`) zur Inspektion bereitgestellt.

---

## 7. Lizenz
Dieses Projekt ist unter der MIT License lizenziert.
