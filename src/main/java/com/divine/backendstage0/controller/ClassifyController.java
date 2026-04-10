package com.divine.backendstage0.controller;

import com.divine.backendstage0.model.GenderizeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")

public class ClassifyController {

    private final RestClient restClient = RestClient.create();

    @GetMapping("/classify")
    public ResponseEntity<Object> classifyName(
            @RequestParam(value = "name", required = false) String name) {

        // 400 — missing or empty
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error",
                            "message", "Missing or empty name parameter"));
        }

        // 422 — name must contain at least one letter (not purely numeric/symbols)
        if (!name.trim().matches(".*[a-zA-Z].*")) {
            return ResponseEntity.unprocessableEntity()
                    .body(Map.of("status", "error",
                            "message", "name must be a valid string"));
        }

        String trimmedName = name.trim();

        try {
            GenderizeResponse apiResponse = restClient.get()
                    .uri("https://api.genderize.io/?name={name}", trimmedName)
                    .retrieve()
                    .body(GenderizeResponse.class);

            // Edge case — no prediction available
            if (apiResponse == null
                    || apiResponse.gender() == null
                    || apiResponse.count() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "error",
                                "message", "No prediction available for the provided name"));
            }

            boolean isConfident =
                    apiResponse.probability() >= 0.7 && apiResponse.count() >= 100;

            // LinkedHashMap preserves field order in the JSON response
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("name", trimmedName);
            data.put("gender", apiResponse.gender());
            data.put("probability", apiResponse.probability());
            data.put("sample_size", apiResponse.count());
            data.put("is_confident", isConfident);
            data.put("processed_at", Instant.now().toString());

            return ResponseEntity.ok(Map.of("status", "success", "data", data));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("status", "error",
                            "message", "Upstream or server failure"));
        }
    }
}
