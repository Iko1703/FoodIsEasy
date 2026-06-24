package com.example.FoodIsEasy.dto;

import java.util.List;

public record NutritionAnalyticsDto(
        int periodDays,
        int totalMeals,
        int uniqueDishes,
        double diversityIndex,
        double avgKcalPerMeal,
        double avgProteinPerMeal,
        double avgFatPerMeal,
        double avgCarbPerMeal,
        List<DishRepeatDto> topRepeated,
        List<String> insights,
        List<DailyNutritionDto> dailyStats,
        List<MealTypeStatDto> mealTypeStats
) {
    public record DishRepeatDto(String title, int count) {}

    public record DailyNutritionDto(
            String date,
            int meals,
            double kcal,
            double protein,
            double fat,
            double carbs
    ) {}

    public record MealTypeStatDto(String mealType, int count) {}
}
