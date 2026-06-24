package com.example.FoodIsEasy.dto;

import com.example.FoodIsEasy.model.enums.ShoppingListStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ShoppingListDto(
        Long id,
        Long mealPlanId,
        String mealPlanName,
        Long groupId,
        String groupName,
        ShoppingListStatus status,
        String orderNote,
        LocalDateTime orderedAt,
        List<ShoppingListItemDto> items
) {
}
