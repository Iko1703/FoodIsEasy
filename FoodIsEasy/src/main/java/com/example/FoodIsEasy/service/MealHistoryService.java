package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.dto.LogMealRequest;
import com.example.FoodIsEasy.dto.MealHistoryDto;

import java.time.LocalDate;
import java.util.List;

public interface MealHistoryService {
    List<MealHistoryDto> getHistory(Long userId, LocalDate from, LocalDate to);
    MealHistoryDto logMeal(Long userId, LogMealRequest request);
    void deleteEntry(Long userId, Long historyId);
}
