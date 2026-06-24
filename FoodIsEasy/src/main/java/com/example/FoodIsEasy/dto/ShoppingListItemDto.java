package com.example.FoodIsEasy.dto;

public record ShoppingListItemDto(
        Long id,
        Long productId,
        String productName,
        Integer quantityGrams,
        boolean checked,
        String customName
) {
}
