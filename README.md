# Selenium Java E-commerce Test Framework

[![Selenium Tests](https://github.com/s4ytv4n/selenium-java-ecommerce-framework/actions/workflows/tests.yml/badge.svg)](https://github.com/s4ytv4n/selenium-java-ecommerce-framework/actions/workflows/tests.yml)
[![Allure report](https://img.shields.io/badge/Allure-report-orange)](https://s4ytv4n.github.io/selenium-java-ecommerce-framework/)

UI and API test automation for an online store in **Java 17 with Selenium 4, TestNG and REST Assured**,
reported with **Allure**. The target is the public demo shop [automationexercise.com](https://automationexercise.com),
which has a real signup, catalogue, search, cart and REST API.

The structure follows the Selenium frameworks I build and maintain for production **Magento 2** stores
(B2C/B2B, multi-store for DE, IT and Scandinavian markets). That client code is private;
this repository shows the same approach on a public target. A TypeScript/Playwright version of the same
suite lives in [playwright-ecommerce-framework](https://github.com/s4ytv4n/playwright-ecommerce-framework).

## What is covered

| Area | UI (Selenium) | API (REST Assured) |
|---|---|---|
| Registration & login | full signup flow with backend check, login/logout, wrong password, duplicate email, HTML5 required fields | create → verify → read → update → delete lifecycle, 400/404/405 cases |
| Catalogue & search | listing, data-driven search compared with the API, empty search | products and brands validated against **JSON Schema**, search consistency |
| Cart | add from listing, quantity from product page, totals, remove item, guest cart kept after login | |

## Design decisions

- **Page Object with fluent navigation.** Actions return the page the user lands on (`login(...)` → `HomePage`), so tests read as user journeys. Locators are `By` constants, every interaction goes through explicit waits — no `Thread.sleep`.
- **Thread-safe parallel runs.** `DriverManager` keeps one `WebDriver` per thread (`ThreadLocal`); TestNG runs methods and data-provider rows in parallel.
- **API for setup, UI for the check.** `registeredUser()` creates an account via the API in milliseconds and the base class deletes it after the test, pass or fail. No test data piles up on the server.
- **UI ↔ API consistency.** Search results and the catalogue in the browser are compared with the API, so nothing is hard-coded and tests survive catalogue changes.
- **Contract checks.** API responses are validated against JSON Schemas in `src/test/resources/schemas`.
- **Stable by design.** Ads, trackers and the consent banner are blocked through the Chrome DevTools Protocol (`Network.setBlockedURLs`) — the main cause of intercepted clicks on this site.
- **Rich failure evidence.** On failure the screenshot, URL and page source are attached to Allure; every page action is an Allure `@Step` and every API call shows its request and response.
- **Configuration without code changes.** `config.properties` defaults, overridable with `-Dkey=value` or environment variables (`BROWSER=firefox`, `BASE_URL=...`).

## Tech stack

Java 17 · Selenium 4 (Selenium Manager) · TestNG · REST Assured · JSON Schema Validator · AssertJ · Datafaker · Allure · Logback · Maven · GitHub Actions · GitHub Pages

## Project structure

```
src/main/java/io/github/s4ytv4n/
  api/       REST Assured client and response wrapper
  config/    layered configuration
  data/      test data factory
  driver/    browser factory (CDP request blocking) and thread-local driver manager
  models/    domain records
  pages/     page objects and shared components
src/test/java/io/github/s4ytv4n/
  base/      base classes: browser lifecycle, failure evidence, test-data cleanup
  api/       API tests
  ui/        UI tests
src/test/resources/
  testng.xml / smoke.xml   suites
  schemas/                 JSON Schemas
```

## Running locally

Requirements: JDK 17+, Maven, Chrome (or Firefox/Edge).

```bash
mvn test                               # full regression, headless Chrome
mvn test -Dsuite=smoke                 # smoke suite
mvn test -Dheadless=false              # watch the browser
mvn test -Dbrowser=firefox             # another browser
mvn test -Dtest=CartTest               # one class
mvn allure:serve                       # open the Allure report
```

## CI

Every push and pull request runs the full suite in headless Chrome on GitHub Actions; a scheduled daily run
catches changes on the live site, and the smoke suite can be started manually. The Allure report is published to
[GitHub Pages](https://s4ytv4n.github.io/selenium-java-ecommerce-framework/).

## Author

**Ivan Saitarly** — QA Automation Engineer · Selenium/Java/TestNG · Playwright/TypeScript · API & load testing · E-commerce
