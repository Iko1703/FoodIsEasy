package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.model.entity.FavoriteDelishies;

import java.util.List;

public interface FavoriteDelishiesService {
    void create(FavoriteDelishies favoriteDelishies);
    List<FavoriteDelishies> readAll();
    FavoriteDelishies read(long id);
    boolean update(FavoriteDelishies favoriteDelishies, long id);
    boolean delete(long id);
}




