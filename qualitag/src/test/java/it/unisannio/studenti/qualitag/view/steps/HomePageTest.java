package it.unisannio.studenti.qualitag.view.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test to check if the home page is shown.
 */
public class HomePageTest {

  private static final Logger logger = LoggerFactory.getLogger(HomePageTest.class);
  private WebDriver driver;
  private String baseUrl = "http://localhost:8080";
  private String urlRedirect = "https://localhost:8443/";

  /**
   * User digits the homepage URL.
   */
  @Given("User digits the homepage URL")
  public void userDigitsHomepageUrl() {
    logger.info("Starting userDigitsHomepageUrl");

    // Use 127.0.0.1 instead of localhost
    baseUrl = baseUrl.replace("localhost", "127.0.0.1");
    urlRedirect = urlRedirect.replace("localhost", "127.0.0.1");


    // System.setProperty("webdriver.gecko.driver",
    //     "src/test/resources/selenium_web_drivers/windows/geckodriver.exe");
    // System.setProperty("webdriver.chrome.driver",
    //     "src/test/resources/selenium_web_drivers/windows/chromedriver.exe");

    FirefoxOptions options = new FirefoxOptions();
    // ChromeOptions options = new ChromeOptions();

    // Set Firefox binary location based on environment
    String firefoxBinary = System.getenv("FIREFOX_BIN");
    if (firefoxBinary == null || firefoxBinary.isEmpty()) {
      // If FIREFOX_BIN is not set, assume Firefox is in the default location
      firefoxBinary = "/usr/bin/firefox"; // Default Linux path
    }
    options.setBinary(firefoxBinary);
    // options.setBinary(System.getenv("GOOGLE_CHROME_BIN"));

    options.addArguments("test-type");
    options.addArguments("--disable-web-security");
    options.addArguments("--allow-running-insecure-content");
    options.addArguments("--ignore-certificate-errors");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--disable-gpu");
    options.addArguments("--headless");

    driver = new FirefoxDriver(options);
    // driver = new ChromeDriver(options);

    logger.info("Accessing URL: " + baseUrl);
    driver.get(baseUrl);

    logger.info("Navigated to: {}", baseUrl);
    logger.info("Finished userDigitsHomepageUrl");
  }

  /**
   * Home page is shown.
   */
  @Then("Home page is shown")
  public void homePageIsShown() {
    System.out.println("Home page is shown test is running");
    logger.info("Starting homePageIsShown");

    String title = driver.getTitle();
    logger.info("Page title: {}", title);

    assertEquals("Home - QualiTag", title);

    // Check redirect
    String currentUrl = driver.getCurrentUrl();
    logger.info("Current URL: {}", currentUrl);
    assertEquals(urlRedirect, currentUrl);

    driver.quit();
    logger.info("Finished homePageIsShown");
  }
}
