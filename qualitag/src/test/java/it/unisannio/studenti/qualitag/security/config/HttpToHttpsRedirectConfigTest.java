package it.unisannio.studenti.qualitag.security.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Test class for HTTP to HTTPS redirection configuration.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpToHttpsRedirectConfigTest {

  /**
   * Tests that an HTTP request is redirected to HTTPS.
   */
  @Test
  public void testHttpToHttpsRedirect() {
    String httpUrl = "http://localhost:8080";
    String httpsUrl = "https://localhost:8443/";

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.getForEntity(httpUrl, String.class);

    assertEquals(HttpStatus.FOUND, response.getStatusCode());
    assertEquals(httpsUrl, response.getHeaders().getLocation().toString());
  }
}
