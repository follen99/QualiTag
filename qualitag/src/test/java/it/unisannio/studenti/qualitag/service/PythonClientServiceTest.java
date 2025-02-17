package it.unisannio.studenti.qualitag.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for PythonClientService.
 */
public class PythonClientServiceTest {

  private MockWebServer mockWebServer;
  private PythonClientService pythonClientService;

  /**
   * Set up the test environment.
   *
   * @throws IOException if an I/O error occurs
   */
  @BeforeEach
  public void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
    
    String baseUrl = mockWebServer.url("/").toString();
    pythonClientService = new PythonClientService(baseUrl);
  }

  /**
   * Tear down the test environment.
   *
   * @throws IOException if an I/O error occurs
   */
  @AfterEach
  public void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  public void testGetKrippendorffAlpha() {
    // Simulate Python server response
    mockWebServer
        .enqueue(new MockResponse().setBody("0.85").addHeader("Content-Type", "application/json"));

    List<List<List<String>>> input = List.of(List.of(List.of("A", "B"), List.of("C", "D")));

    // Call method
    String result = pythonClientService.getKrippendorffAlpha(input);

    // Verify response
    assertEquals("0.85", result);
  }

  @Test
  public void testProcessTags() {
    // Simulate Python server response
    mockWebServer.enqueue(
        new MockResponse().setBody("processed_tags").addHeader("Content-Type", "application/json"));

    List<String> tags = List.of("tag1", "tag2");

    // Call method
    String result = pythonClientService.processTags(tags);

    // Verify response
    assertEquals("processed_tags", result);
  }
}
