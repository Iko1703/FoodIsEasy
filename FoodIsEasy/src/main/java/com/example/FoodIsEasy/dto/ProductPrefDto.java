package com.example.FoodIsEasy.dto;

import com.example.FoodIsEasy.model.enums.ProductPreferenceType;

public record ProductPrefDto(Long productId, String productName, ProductPreferenceType prefType) {
}
