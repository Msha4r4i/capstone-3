package com.example.substracker.Controller;

import com.example.substracker.Model.AiAnalysis;
import com.example.substracker.Repository.AiAnalysisRepository;
import com.example.substracker.Service.AiAnalysisService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/ai_analysis")

public class AiAnalysisController {
    private final AiAnalysisService aiAnalysisService;
    //Made by Hassan
    @GetMapping("/get_ai_analysis_by_user_id/{userId}")
    public ResponseEntity<?> getAiAnalysisByUserId(@PathVariable Integer userId){
        return ResponseEntity
                .status(200)
                .body(aiAnalysisService.getAiAnalysisByUserId(userId));
    }

    @GetMapping("/get/user/{userId}/dto")
    public ResponseEntity<?> getAiAnalysisDTOByUserId(@PathVariable Integer userId){
        return ResponseEntity.status(200).body(aiAnalysisService.getAiAnalysisDTOOutByUserId(userId));
    }

}
