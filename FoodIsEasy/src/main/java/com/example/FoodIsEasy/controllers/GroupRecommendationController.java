package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.dto.RecommendationDto;
import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.service.CurrentUserService;
import com.example.FoodIsEasy.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups/{groupId}/recommendations")
public class GroupRecommendationController {

    private final CurrentUserService currentUserService;
    private final RecommendationService recommendationService;

    public GroupRecommendationController(CurrentUserService currentUserService, RecommendationService recommendationService) {
        this.currentUserService = currentUserService;
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<List<RecommendationDto>> group(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "50") int limit) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(recommendationService.getGroupRecommendations(user.getId(), groupId, limit));
    }
}
