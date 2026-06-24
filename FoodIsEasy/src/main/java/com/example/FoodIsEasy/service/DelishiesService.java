package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.dto.DelishiesSearchCriteria;
import com.example.FoodIsEasy.model.entity.Delishies;

import java.util.List;
import java.util.Set;

public interface DelishiesService {
    void create(Delishies delishies);
    List<Delishies> readAll();
    List<Delishies> search(DelishiesSearchCriteria criteria, Long userId, Set<Long> favoriteIds);
    Delishies read(long id);
    boolean update(Delishies delishies, long id);
    boolean delete(long id);
}



