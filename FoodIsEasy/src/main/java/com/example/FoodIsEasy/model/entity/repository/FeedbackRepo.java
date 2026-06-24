package com.example.FoodIsEasy.model.entity.repository;

import com.example.FoodIsEasy.model.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepo extends JpaRepository<Feedback, Long> {

    @org.springframework.data.jpa.repository.Query(
            "SELECT f FROM Feedback f JOIN FETCH f.author WHERE f.delishies.id = :delishiesId ORDER BY f.createdAt DESC")
    java.util.List<Feedback> findByDelishiesIdWithAuthor(
            @org.springframework.data.repository.query.Param("delishiesId") Long delishiesId);

    @org.springframework.data.jpa.repository.Query(
            "SELECT AVG(f.rating) FROM Feedback f WHERE f.delishies.id = :delishiesId AND f.rating IS NOT NULL")
    Double avgRatingForDelishies(
            @org.springframework.data.repository.query.Param("delishiesId") Long delishiesId);
}



