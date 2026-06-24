package com.example.FoodIsEasy.dto;

import java.util.List;

public record RecommendationDto(
        Long delishiesId,
        String title,
        String description,
        String imageUrl,
        Integer cookTimeMinutes,
        Integer kcalTotal,
        Double score,
        List<String> reasons
) {
}
