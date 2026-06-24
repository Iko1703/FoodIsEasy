package com.example.FoodIsEasy.model.entity.repository;

import com.example.FoodIsEasy.model.entity.ShoppingList;
import com.example.FoodIsEasy.model.enums.ShoppingListStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShoppingListRepo extends JpaRepository<ShoppingList, Long> {

    Optional<ShoppingList> findByMealPlanId(Long mealPlanId);

    @Query("SELECT DISTINCT s FROM ShoppingList s LEFT JOIN FETCH s.items i LEFT JOIN FETCH i.product WHERE s.id = :id")
    Optional<ShoppingList> findByIdWithItems(@Param("id") Long id);

    List<ShoppingList> findByGroupIdAndStatus(Long groupId, ShoppingListStatus status);
}
