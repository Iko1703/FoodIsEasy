package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.dto.NutritionAnalyticsDto;
import com.example.FoodIsEasy.dto.NutritionAnalyticsDto.DishRepeatDto;
import com.example.FoodIsEasy.model.entity.MealHistory;
import com.example.FoodIsEasy.model.entity.repository.MealHistoryRepo;
import com.example.FoodIsEasy.service.NutritionAnalyticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.FoodIsEasy.dto.NutritionAnalyticsDto.DailyNutritionDto;
import com.example.FoodIsEasy.dto.NutritionAnalyticsDto.MealTypeStatDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NutritionAnalyticsServiceImpl implements NutritionAnalyticsService {

    private final MealHistoryRepo mealHistoryRepo;

    public NutritionAnalyticsServiceImpl(MealHistoryRepo mealHistoryRepo) {
        this.mealHistoryRepo = mealHistoryRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public NutritionAnalyticsDto analyze(Long userId, int days) {
        int period = Math.max(7, Math.min(90, days));
        LocalDateTime from = LocalDate.now().minusDays(period).atStartOfDay();
        LocalDateTime to = LocalDate.now().atTime(LocalTime.MAX);

        List<MealHistory> history = mealHistoryRepo.findByUserIdAndEatenAtBetweenOrderByEatenAtDesc(userId, from, to);
        if (history.isEmpty()) {
            return empty(period);
        }

        int total = history.size();
        Map<String, Long> dishCounts = history.stream()
                .collect(Collectors.groupingBy(h -> h.getDelishies().getTitle(), Collectors.counting()));
        int unique = dishCounts.size();
        double diversityIndex = total > 0 ? Math.round((unique * 100.0 / total) * 10.0) / 10.0 : 0;

        double sumKcal = 0, sumP = 0, sumF = 0, sumC = 0;
        int withNutrition = 0;
        for (MealHistory h : history) {
            var d = h.getDelishies();
            if (d.getKcalTotal() != null) {
                sumKcal += d.getKcalTotal();
                sumP += d.getProteinTotal() != null ? d.getProteinTotal() : 0;
                sumF += d.getFatTotal() != null ? d.getFatTotal() : 0;
                sumC += d.getCarbTotal() != null ? d.getCarbTotal() : 0;
                withNutrition++;
            }
        }

        double avgKcal = withNutrition > 0 ? sumKcal / withNutrition : 0;
        double avgP = withNutrition > 0 ? sumP / withNutrition : 0;
        double avgF = withNutrition > 0 ? sumF / withNutrition : 0;
        double avgC = withNutrition > 0 ? sumC / withNutrition : 0;

        List<DishRepeatDto> topRepeated = dishCounts.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(e -> new DishRepeatDto(e.getKey(), e.getValue().intValue()))
                .toList();

        List<DailyNutritionDto> dailyStats = buildDailyStats(history, period);
        List<MealTypeStatDto> mealTypeStats = history.stream()
                .collect(Collectors.groupingBy(h -> h.getMealType().name(), Collectors.counting()))
                .entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(e -> new MealTypeStatDto(e.getKey(), e.getValue().intValue()))
                .toList();

        List<String> insights = new ArrayList<>();
        if (diversityIndex < 50) {
            insights.add("Рацион однообразный — попробуйте новые блюда из рекомендаций");
        } else {
            insights.add("Хорошее разнообразие блюд за период");
        }
        if (!topRepeated.isEmpty()) {
            insights.add("Часто повторяется: " + topRepeated.get(0).title());
        }
        if (avgP < 15) {
            insights.add("Возможен недостаток белка — добавьте мясные или молочные блюда");
        }
        if (avgC > 60) {
            insights.add("Много углеводов — чередуйте с овощными блюдами");
        }
        if (avgKcal > 600) {
            insights.add("Средняя калорийность приёма пищи высокая");
        }

        return new NutritionAnalyticsDto(
                period, total, unique, diversityIndex,
                round(avgKcal), round(avgP), round(avgF), round(avgC),
                topRepeated, insights, dailyStats, mealTypeStats);
    }

    private List<DailyNutritionDto> buildDailyStats(List<MealHistory> history, int period) {
        Map<LocalDate, List<MealHistory>> byDay = history.stream()
                .collect(Collectors.groupingBy(h -> h.getEatenAt().toLocalDate()));

        List<DailyNutritionDto> result = new ArrayList<>();
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(period - 1L);
        for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
            List<MealHistory> dayMeals = byDay.getOrDefault(day, List.of());
            double kcal = 0, protein = 0, fat = 0, carbs = 0;
            for (MealHistory h : dayMeals) {
                var d = h.getDelishies();
                if (d.getKcalTotal() != null) {
                    kcal += d.getKcalTotal();
                    protein += d.getProteinTotal() != null ? d.getProteinTotal() : 0;
                    fat += d.getFatTotal() != null ? d.getFatTotal() : 0;
                    carbs += d.getCarbTotal() != null ? d.getCarbTotal() : 0;
                }
            }
            result.add(new DailyNutritionDto(
                    day.toString(),
                    dayMeals.size(),
                    round(kcal),
                    round(protein),
                    round(fat),
                    round(carbs)));
        }
        return result;
    }

    private NutritionAnalyticsDto empty(int period) {
        List<DailyNutritionDto> daily = buildDailyStats(List.of(), period);
        return new NutritionAnalyticsDto(
                period, 0, 0, 0, 0, 0, 0, 0,
                List.of(),
                List.of("Записей в истории пока нет — отмечайте блюда, чтобы увидеть аналитику"),
                daily,
                List.of());
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
