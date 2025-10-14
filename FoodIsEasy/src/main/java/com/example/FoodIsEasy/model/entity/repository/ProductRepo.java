package com.example.FoodIsEasy.model.entity.repository;

import com.example.FoodIsEasy.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Long> {
}



