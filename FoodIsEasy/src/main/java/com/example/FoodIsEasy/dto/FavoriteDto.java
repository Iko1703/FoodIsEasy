package com.example.FoodIsEasy.dto;

public record FavoriteDto(
        Long delishiesId,
        String title,
        String imageUrl,
        Integer kcalTotal,
        Double avgRating
) {
}
