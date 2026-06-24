package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.dto.MarkOrderedRequest;
import com.example.FoodIsEasy.dto.ShoppingListDto;

public interface ShoppingListService {
    ShoppingListDto generateFromMealPlan(Long userId, Long mealPlanId);
    ShoppingListDto getList(Long userId, Long listId);
    ShoppingListDto getByMealPlan(Long userId, Long mealPlanId);
    ShoppingListDto toggleItem(Long userId, Long listId, Long itemId, boolean checked);
    ShoppingListDto markOrdered(Long userId, Long listId, MarkOrderedRequest request);
}
