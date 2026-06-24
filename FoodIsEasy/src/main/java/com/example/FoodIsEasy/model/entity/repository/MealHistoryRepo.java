package com.example.FoodIsEasy.model.entity.repository;

import com.example.FoodIsEasy.model.entity.MealHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MealHistoryRepo extends JpaRepository<MealHistory, Long> {

    List<MealHistory> findByUserIdAndEatenAtBetweenOrderByEatenAtDesc(
            Long userId, LocalDateTime from, LocalDateTime to);

    List<MealHistory> findTop50ByUserIdOrderByEatenAtDesc(Long userId);

    @Query("SELECT DISTINCT h.delishies.id FROM MealHistory h WHERE h.user.id = :userId AND h.eatenAt >= :since")
    List<Long> findRecentDishIds(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
