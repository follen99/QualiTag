package it.unisannio.studenti.qualitag.view;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UiTest {

  private WebDriver driver;

  @BeforeEach
  public void setUp() {
    // Set the path to the GeckoDriver executable
    System.setProperty("webdriver.gecko.driver", "drivers/chromedriver.exe");
    driver = new ChromeDriver();

    // Configure ChromeOptions to ignore SSL certificate errors
    ChromeOptions options = new ChromeOptions();
    options.setAcceptInsecureCerts(true);

    driver = new ChromeDriver(options);
  }

  @AfterEach
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Test
  public void testHomePageTitle() {
    driver.get("http://localhost:8080"); // URL of your application
    String title = driver.getTitle();
    assertEquals("Home - QualiTag", title);
  }

}