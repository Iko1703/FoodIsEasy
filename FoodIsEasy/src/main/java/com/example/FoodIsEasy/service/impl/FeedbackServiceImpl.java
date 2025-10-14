package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.model.entity.Feedback;
import com.example.FoodIsEasy.model.entity.repository.FeedbackRepo;
import com.example.FoodIsEasy.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepo feedbackRepo;

    @Override
    public void create(Feedback feedback) {
        feedbackRepo.save(feedback);
    }

    @Override
    public List<Feedback> readAll() {
        return feedbackRepo.findAll();
    }

    @Override
    public Feedback read(long id) {
        return feedbackRepo.findById(id).orElse(null);
    }

    @Override
    public boolean update(Feedback feedback, long id) {
        if (feedbackRepo.existsById(id)) {
            feedback.setId(id);
            feedbackRepo.save(feedback);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(long id) {
        if (feedbackRepo.existsById(id)) {
            feedbackRepo.deleteById(id);
            return true;
        }
        return false;
    }
}




