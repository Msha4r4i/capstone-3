package com.example.substracker.Controller;

import com.example.substracker.Model.AiSubscriptionAlternative;
import com.example.substracker.Service.AiSubscriptionAlternativeService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai")
public class AiSubscriptionAlternativeController {

    private final AiSubscriptionAlternativeService aiSubscriptionAlternativeService;
    //made by Hassan
    // Generate & return an AI alternative for a given subscription (saved inside the service)
    @GetMapping("/alternative/{subscriptionId}")
    public ResponseEntity<?> getAiSubAlternativeSubscriptionId(@PathVariable Integer subscriptionId) {
        AiSubscriptionAlternative alt =
                aiSubscriptionAlternativeService.getAiSubscriptionAlternativeBySubscriptionId(subscriptionId);
        return ResponseEntity.status(200).body(alt);
    }

    @GetMapping("/get/Ai-subscription-alternative-DTO/{subscriptionId}")
    public ResponseEntity<?> getAiAlterDTOBySubId(@PathVariable Integer subscriptionId){
        return ResponseEntity.status(200).body(aiSubscriptionAlternativeService.getAiSubscriptionAlternativeDTOOutBySubscriptionId(subscriptionId));
    }
}
