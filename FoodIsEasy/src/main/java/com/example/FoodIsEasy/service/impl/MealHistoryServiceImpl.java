package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.dto.LogMealRequest;
import com.example.FoodIsEasy.dto.MealHistoryDto;
import com.example.FoodIsEasy.model.entity.Delishies;
import com.example.FoodIsEasy.model.entity.MealHistory;
import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.model.entity.repository.DelishiesRepo;
import com.example.FoodIsEasy.model.entity.repository.MealHistoryRepo;
import com.example.FoodIsEasy.model.entity.repository.UserRepo;
import com.example.FoodIsEasy.service.MealHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class MealHistoryServiceImpl implements MealHistoryService {

    private final MealHistoryRepo mealHistoryRepo;
    private final UserRepo userRepo;
    private final DelishiesRepo delishiesRepo;

    public MealHistoryServiceImpl(MealHistoryRepo mealHistoryRepo, UserRepo userRepo, DelishiesRepo delishiesRepo) {
        this.mealHistoryRepo = mealHistoryRepo;
        this.userRepo = userRepo;
        this.delishiesRepo = delishiesRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MealHistoryDto> getHistory(Long userId, LocalDate from, LocalDate to) {
        LocalDateTime fromDt = (from != null ? from : LocalDate.now().minusDays(30)).atStartOfDay();
        LocalDateTime toDt = (to != null ? to : LocalDate.now()).atTime(LocalTime.MAX);
        return mealHistoryRepo.findByUserIdAndEatenAtBetweenOrderByEatenAtDesc(userId, fromDt, toDt)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public MealHistoryDto logMeal(Long userId, LogMealRequest request) {
        if (request.delishiesId() == null || request.mealType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Укажите блюдо и тип приёма пищи");
        }
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        Delishies dish = delishiesRepo.findById(request.delishiesId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Блюдо не найдено"));

        MealHistory history = new MealHistory();
        history.setUser(user);
        history.setDelishies(dish);
        history.setMealType(request.mealType());
        history.setEatenAt(request.eatenAt() != null ? request.eatenAt() : LocalDateTime.now());
        return toDto(mealHistoryRepo.save(history));
    }

    @Override
    @Transactional
    public void deleteEntry(Long userId, Long historyId) {
        MealHistory history = mealHistoryRepo.findById(historyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись не найдена"));
        if (!history.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа");
        }
        mealHistoryRepo.delete(history);
    }

    private MealHistoryDto toDto(MealHistory h) {
        return new MealHistoryDto(
                h.getId(),
                h.getDelishies().getId(),
                h.getDelishies().getTitle(),
                h.getDelishies().getImageUrl(),
                h.getMealType(),
                h.getEatenAt());
    }
}
