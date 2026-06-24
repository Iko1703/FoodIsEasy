package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.dto.DelishiesSearchCriteria;
import com.example.FoodIsEasy.model.entity.Delishies;
import com.example.FoodIsEasy.model.entity.repository.DelishiesRepo;
import com.example.FoodIsEasy.model.entity.repository.FeedbackRepo;
import com.example.FoodIsEasy.service.DelishiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DelishiesServiceImpl implements DelishiesService {

    private final DelishiesRepo delishiesRepo;
    private final FeedbackRepo feedbackRepo;

    @Override
    public void create(Delishies delishies) {
        delishiesRepo.save(delishies);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Delishies> readAll() {
        return prepareForList(delishiesRepo.findAllWithDetails());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Delishies> search(DelishiesSearchCriteria criteria, Long userId, Set<Long> favoriteIds) {
        return prepareForList(delishiesRepo.findAllWithDetails().stream()
                .filter(d -> matches(d, criteria, favoriteIds))
                .toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Delishies read(long id) {
        Delishies dish = delishiesRepo.findByIdWithProducts(id).orElse(null);
        if (dish != null) {
            dish.setFeedbacks(feedbackRepo.findByDelishiesIdWithAuthor(id));
        }
        return dish;
    }

    @Override
    public boolean update(Delishies delishies, long id) {
        if (delishiesRepo.existsById(id)) {
            delishies.setId(id);
            delishiesRepo.save(delishies);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(long id) {
        if (delishiesRepo.existsById(id)) {
            delishiesRepo.deleteById(id);
            return true;
        }
        return false;
    }

    private List<Delishies> prepareForList(List<Delishies> dishes) {
        dishes.forEach(d -> d.setFeedbacks(Collections.emptyList()));
        return dishes;
    }

    private boolean matches(Delishies d, DelishiesSearchCriteria c, Set<Long> favoriteIds) {
        if (c.q() != null && !c.q().isBlank()) {
            String q = c.q().trim().toLowerCase(Locale.ROOT);
            boolean inTitle = d.getTitle() != null && d.getTitle().toLowerCase(Locale.ROOT).contains(q);
            boolean inDesc = d.getDescription() != null && d.getDescription().toLowerCase(Locale.ROOT).contains(q);
            if (!inTitle && !inDesc) {
                return false;
            }
        }
        if (c.cuisineId() != null) {
            if (d.getCuisine() == null || !c.cuisineId().equals(d.getCuisine().getId())) {
                return false;
            }
        }
        if (c.categoryId() != null) {
            if (d.getCategory() == null || !c.categoryId().equals(d.getCategory().getId())) {
                return false;
            }
        }
        if (c.mealRole() != null && d.getMealRole() != c.mealRole()) {
            return false;
        }
        if (c.maxKcal() != null) {
            if (d.getKcalTotal() == null || d.getKcalTotal() > c.maxKcal()) {
                return false;
            }
        }
        if (Boolean.TRUE.equals(c.favoritesOnly())) {
            return favoriteIds != null && favoriteIds.contains(d.getId());
        }
        return true;
    }
}
