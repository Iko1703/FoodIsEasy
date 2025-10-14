package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.model.entity.Feedback;
import com.example.FoodIsEasy.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/feedbacks")
    public ResponseEntity<?> create(@RequestBody Feedback feedback) {
        feedbackService.create(feedback);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/feedbacks")
    public ResponseEntity<List<Feedback>> readAll() {
        List<Feedback> all = feedbackService.readAll();
        return all != null && !all.isEmpty() ? new ResponseEntity<>(all, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/feedbacks/{id}")
    public ResponseEntity<?> read(@PathVariable("id") long id) {
        Feedback item = feedbackService.read(id);
        return item != null ? new ResponseEntity<>(item, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/feedbacks/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long id, @RequestBody Feedback feedback) {
        boolean updated = feedbackService.update(feedback, id);
        return updated ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/feedbacks/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        boolean deleted = feedbackService.delete(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}




