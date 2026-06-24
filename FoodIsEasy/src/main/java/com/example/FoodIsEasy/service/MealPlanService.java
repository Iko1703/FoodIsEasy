package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.dto.*;

import java.util.List;

public interface MealPlanService {
    List<MealPlanSummaryDto> listPersonalPlans(Long userId);
    List<MealPlanSummaryDto> listGroupPlans(Long userId, Long groupId);
    MealPlanDetailDto getPlan(Long userId, Long planId);
    MealPlanDetailDto createPlan(Long userId, CreateMealPlanRequest request);
    MealPlanDetailDto setEntry(Long userId, Long planId, SetMealPlanEntryRequest request);
    void removeEntry(Long userId, Long planId, Long entryId);
    MealPlanDetailDto generatePlan(Long userId, Long planId);
    void deletePlan(Long userId, Long planId);
}
