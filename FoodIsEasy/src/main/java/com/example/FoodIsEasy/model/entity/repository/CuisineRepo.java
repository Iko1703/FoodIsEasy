package com.example.FoodIsEasy.model.entity.repository;

import com.example.FoodIsEasy.model.entity.Cuisine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuisineRepo extends JpaRepository<Cuisine, Long> {
}
