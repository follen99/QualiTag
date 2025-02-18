package it.unisannio.studenti.qualitag.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


class TestHealthController {

  @Test
  void testPing() {
    HealthController healthController = new HealthController();
    ResponseEntity<?> response = healthController.ping();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());

    @SuppressWarnings("unchecked")
    Map<String, String> body = (Map<String, String>) response.getBody();
    assertEquals("Server is up and running", body.get("msg"));
  }
}
