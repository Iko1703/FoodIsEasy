package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.dto.RecommendationDto;

import java.util.List;

public interface RecommendationService {
    List<RecommendationDto> getPersonalRecommendations(Long userId, int limit);
    List<RecommendationDto> getGroupRecommendations(Long userId, Long groupId, int limit);
}
