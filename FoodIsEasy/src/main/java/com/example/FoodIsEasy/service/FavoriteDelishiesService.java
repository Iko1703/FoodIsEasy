package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.dto.FavoriteDto;
import com.example.FoodIsEasy.model.entity.FavoriteDelishies;

import java.util.List;
import java.util.Set;

public interface FavoriteDelishiesService {
    void create(FavoriteDelishies favoriteDelishies);
    List<FavoriteDelishies> readAll();
    FavoriteDelishies read(long id);
    boolean update(FavoriteDelishies favoriteDelishies, long id);
    boolean delete(long id);

    List<FavoriteDto> listForUser(Long userId);
    Set<Long> favoriteIdsForUser(Long userId);
    FavoriteDto add(Long userId, Long delishiesId);
    void remove(Long userId, Long delishiesId);
    boolean isFavorite(Long userId, Long delishiesId);
}




