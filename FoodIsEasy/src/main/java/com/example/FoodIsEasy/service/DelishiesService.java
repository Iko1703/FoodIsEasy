package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.model.entity.Delishies;

import java.util.List;

public interface DelishiesService {
    void create(Delishies delishies);
    List<Delishies> readAll();
    Delishies read(long id);
    boolean update(Delishies delishies, long id);
    boolean delete(long id);
}



