package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.dto.CreateFeedbackRequest;
import com.example.FoodIsEasy.model.entity.Delishies;
import com.example.FoodIsEasy.model.entity.Feedback;
import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.model.entity.repository.DelishiesRepo;
import com.example.FoodIsEasy.model.entity.repository.FeedbackRepo;
import com.example.FoodIsEasy.model.entity.repository.UserRepo;
import com.example.FoodIsEasy.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepo feedbackRepo;
    private final UserRepo userRepo;
    private final DelishiesRepo delishiesRepo;

    public FeedbackServiceImpl(FeedbackRepo feedbackRepo, UserRepo userRepo, DelishiesRepo delishiesRepo) {
        this.feedbackRepo = feedbackRepo;
        this.userRepo = userRepo;
        this.delishiesRepo = delishiesRepo;
    }

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

    @Override
    @Transactional
    public Feedback createForUser(Long userId, Long delishiesId, CreateFeedbackRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        Delishies dish = delishiesRepo.findById(delishiesId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Блюдо не найдено"));

        Feedback feedback = new Feedback();
        feedback.setAuthor(user);
        feedback.setDelishies(dish);
        feedback.setMessage(request.message().trim());
        feedback.setRating(request.rating());
        feedback.setCreatedAt(LocalDateTime.now());
        feedbackRepo.save(feedback);

        Double avg = feedbackRepo.avgRatingForDelishies(delishiesId);
        if (avg != null) {
            dish.setAvgRating(Math.round(avg * 10.0) / 10.0);
            delishiesRepo.save(dish);
        }

        return feedback;
    }
}
