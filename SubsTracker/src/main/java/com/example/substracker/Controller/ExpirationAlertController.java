package com.example.substracker.Controller;

import com.example.substracker.API.ApiResponse;
import com.example.substracker.Service.ExpirationAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/expiration-alert")
@RequiredArgsConstructor
public class ExpirationAlertController {

    private final ExpirationAlertService expirationAlertService;

    /**
     * Trigger manual alert for specific subscription
     */
    //made by Mohammed
    //for testing purposes, to manually send an alert for a specific subscription
    @PostMapping("/send/{subscriptionId}/{alertType}")
    public ResponseEntity<?> sendManualAlert(
            @PathVariable Integer subscriptionId,
            @PathVariable String alertType
    ) {
        expirationAlertService.sendManualAlert(subscriptionId, alertType);
        return ResponseEntity.status(200).body(new ApiResponse("Manual alert sent successfully for subscription ID: " + subscriptionId));
    }
}