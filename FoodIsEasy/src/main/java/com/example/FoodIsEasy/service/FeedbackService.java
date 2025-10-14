package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.model.entity.Feedback;

import java.util.List;

public interface FeedbackService {
    void create(Feedback feedback);
    List<Feedback> readAll();
    Feedback read(long id);
    boolean update(Feedback feedback, long id);
    boolean delete(long id);
}




