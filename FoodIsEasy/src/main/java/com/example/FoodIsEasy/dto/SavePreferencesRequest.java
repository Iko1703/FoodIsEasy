package com.example.FoodIsEasy.dto;

import com.example.FoodIsEasy.model.enums.ProductPreferenceType;

import java.util.List;

public record SavePreferencesRequest(
        List<CuisinePrefInput> cuisines,
        List<ProductPrefInput> products
) {
    public record CuisinePrefInput(Long cuisineId, Integer weight) {
    }

    public record ProductPrefInput(Long productId, ProductPreferenceType prefType) {
    }
}
