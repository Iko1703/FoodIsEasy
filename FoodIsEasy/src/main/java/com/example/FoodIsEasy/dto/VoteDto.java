package com.example.FoodIsEasy.dto;

import com.example.FoodIsEasy.model.enums.VoteStatus;

import java.time.LocalDateTime;
import java.util.List;

public record VoteDto(
        Long id,
        String title,
        VoteStatus status,
        Long groupId,
        List<VoteOptionDto> options,
        boolean userVoted,
        LocalDateTime createdAt,
        Long winnerOptionId,
        Long winnerDelishiesId,
        String winnerDelishiesTitle
) {
}
