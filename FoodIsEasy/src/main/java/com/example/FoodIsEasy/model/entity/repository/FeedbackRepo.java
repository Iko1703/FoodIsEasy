package com.example.FoodIsEasy.model.entity.repository;

import com.example.FoodIsEasy.model.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepo extends JpaRepository<Feedback, Long> {
}



