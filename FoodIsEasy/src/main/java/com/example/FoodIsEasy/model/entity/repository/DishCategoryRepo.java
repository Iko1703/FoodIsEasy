package com.example.FoodIsEasy.model.entity.repository;

import com.example.FoodIsEasy.model.entity.DishCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishCategoryRepo extends JpaRepository<DishCategory, Long> {
}
