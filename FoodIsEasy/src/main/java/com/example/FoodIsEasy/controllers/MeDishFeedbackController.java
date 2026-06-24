package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.dto.CreateFeedbackRequest;
import com.example.FoodIsEasy.model.entity.Feedback;
import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.service.CurrentUserService;
import com.example.FoodIsEasy.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me/delishies")
public class MeDishFeedbackController {

    private final CurrentUserService currentUserService;
    private final FeedbackService feedbackService;

    public MeDishFeedbackController(CurrentUserService currentUserService, FeedbackService feedbackService) {
        this.currentUserService = currentUserService;
        this.feedbackService = feedbackService;
    }

    @PostMapping("/{delishiesId}/feedbacks")
    public ResponseEntity<Feedback> create(
            @PathVariable Long delishiesId,
            @Valid @RequestBody CreateFeedbackRequest request) {
        User user = currentUserService.requireCurrentUser();
        Feedback feedback = feedbackService.createForUser(user.getId(), delishiesId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(feedback);
    }
}
