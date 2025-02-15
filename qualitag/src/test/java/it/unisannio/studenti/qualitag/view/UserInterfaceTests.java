package it.unisannio.studenti.qualitag.view;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URL;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Cucumber.class)
@CucumberOptions(
  features = "src/test/resources/features",
  glue = "it.unisannio.studenti.qualitag.view.steps",
  plugin = {"pretty", "html:build/cucumber/cucumber-reports.html"},
  monochrome = true,
  dryRun = false,
  tags = "@Selenium"
)
public class UserInterfaceTests {

  private static final Logger logger = LoggerFactory.getLogger(UserInterfaceTests.class);
  private static Process serverProcess;

  @BeforeClass
  public static void startServer() throws IOException {
    // Start the server from jar
    logger.info("Starting server...");
    String[] command = {"java", "-jar", "build/libs/qualitag-0.0.1-SNAPSHOT.jar"};
    serverProcess = new ProcessBuilder(command).start();

    // Wait for the server to fully start
    waitForServerStartup();
    logger.info("Server started.");
  }

  @AfterClass
  public static void stopServer() throws IOException {
    logger.info("Stopping server...");
    if (serverProcess != null) {
      serverProcess.destroy();
    }
    logger.info("Server stopped.");
  }

  private static void waitForServerStartup() {
    int port = 8443;
    long startTime = System.currentTimeMillis();
    long timeout = 30000;
    String healthEndpoint = "https://localhost:" + port + "/ping";
    URI uri = URI.create(healthEndpoint);

    // Create a trust manager that does not validate certificate chains
    TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
      }

      public void checkClientTrusted(X509Certificate[] certs, String authType) {}

      public void checkServerTrusted(X509Certificate[] certs, String authType) {}
    }};

    // Install the all-trusting trust manager
    try {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true); // Bypass hostname
                                                                                  // verification
    } catch (Exception e) {
      logger.error("Error setting up trust-all: " + e.getMessage());
      return;
    }

    while (System.currentTimeMillis() - startTime < timeout) {
      try {
        URL url = uri.toURL();
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        // Bypass hostname verification
        connection.setHostnameVerifier((hostname, session) -> true);
        int responseCode = connection.getResponseCode();

        if (responseCode == 200 || responseCode == 302) {
          logger.info("Server is ready and health endpoint is responding! - Code: " + responseCode);
          return;
        } else {
          logger.warn("Health endpoint returned: " + responseCode + ". Retrying...");
        }
      } catch (ConnectException e) {
        logger.info("Server not yet ready, retrying...");
      } catch (IOException e) {
        logger.error("Error checking server status: " + e.getMessage());
        return;
      }

      try {
        Thread.sleep(1000);
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
        return;
      }
    }

    logger.error("Server startup timed out!");
  }
}
