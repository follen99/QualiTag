package it.unisannio.studenti.qualitag.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    
    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        Map<String, String> response = new HashMap<>();

        response.put("msg", "Server is up and running");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
