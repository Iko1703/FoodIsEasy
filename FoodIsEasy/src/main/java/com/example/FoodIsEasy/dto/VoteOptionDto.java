package com.example.FoodIsEasy.dto;

public record VoteOptionDto(
        Long optionId,
        Long delishiesId,
        String delishiesTitle,
        String imageUrl,
        long voteCount
) {
}
