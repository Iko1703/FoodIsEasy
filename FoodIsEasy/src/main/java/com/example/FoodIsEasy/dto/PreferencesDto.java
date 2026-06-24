package com.example.FoodIsEasy.dto;

import java.util.List;

public record PreferencesDto(
        List<CuisinePrefDto> cuisines,
        List<ProductPrefDto> products
) {
}
