package com.example.FoodIsEasy.model.entity.repository;

import com.example.FoodIsEasy.model.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MealPlanRepo extends JpaRepository<MealPlan, Long> {

    List<MealPlan> findByUserIdOrderByStartDateDesc(Long userId);

    List<MealPlan> findByGroupIdOrderByStartDateDesc(Long groupId);

    @Query("SELECT DISTINCT p FROM MealPlan p LEFT JOIN FETCH p.entries e LEFT JOIN FETCH e.delishies WHERE p.id = :id")
    Optional<MealPlan> findByIdWithEntries(@Param("id") Long id);
}
