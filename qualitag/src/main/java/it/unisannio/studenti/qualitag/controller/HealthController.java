package it.unisannio.studenti.qualitag.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is used to check the health of the server.
 */
@RestController
public class HealthController {

  /**
   * Checks if the server is up and running.
   *
   * @return a response entity with the message "Server is up and running"
   */
  @GetMapping("/ping")
  public ResponseEntity<?> ping() {
    Map<String, String> response = new HashMap<>();

    response.put("msg", "Server is up and running");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
