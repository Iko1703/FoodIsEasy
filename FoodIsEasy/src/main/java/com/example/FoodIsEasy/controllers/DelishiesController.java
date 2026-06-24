package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.dto.DelishiesSearchCriteria;
import com.example.FoodIsEasy.model.entity.Delishies;
import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.model.enums.MealRole;
import com.example.FoodIsEasy.service.CurrentUserService;
import com.example.FoodIsEasy.service.DelishiesService;
import com.example.FoodIsEasy.service.FavoriteDelishiesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
public class DelishiesController {
    private final DelishiesService delishiesService;
    private final CurrentUserService currentUserService;
    private final FavoriteDelishiesService favoriteDelishiesService;

    public DelishiesController(
            DelishiesService delishiesService,
            CurrentUserService currentUserService,
            FavoriteDelishiesService favoriteDelishiesService) {
        this.delishiesService = delishiesService;
        this.currentUserService = currentUserService;
        this.favoriteDelishiesService = favoriteDelishiesService;
    }

    @PostMapping("/delishies")
    public ResponseEntity<?> create(@RequestBody Delishies delishies) {
        delishiesService.create(delishies);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/delishies")
    public ResponseEntity<List<Delishies>> readAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long cuisineId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) MealRole mealRole,
            @RequestParam(required = false) Integer maxKcal,
            @RequestParam(required = false) Boolean favoritesOnly) {
        User user = currentUserService.getCurrentUserOrNull();
        if (Boolean.TRUE.equals(favoritesOnly) && user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Войдите, чтобы фильтровать избранное");
        }
        Set<Long> favoriteIds = user != null
                ? favoriteDelishiesService.favoriteIdsForUser(user.getId())
                : Collections.emptySet();

        boolean hasFilters = (q != null && !q.isBlank())
                || cuisineId != null
                || categoryId != null
                || mealRole != null
                || maxKcal != null
                || Boolean.TRUE.equals(favoritesOnly);

        List<Delishies> result;
        if (hasFilters) {
            DelishiesSearchCriteria criteria = new DelishiesSearchCriteria(
                    q, cuisineId, categoryId, mealRole, maxKcal, favoritesOnly);
            result = delishiesService.search(criteria, user != null ? user.getId() : null, favoriteIds);
        } else {
            result = delishiesService.readAll();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/delishies/{id}")
    public ResponseEntity<?> read(@PathVariable("id") long id) {
        Delishies item = delishiesService.read(id);
        return item != null ? new ResponseEntity<>(item, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/delishies/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long id, @RequestBody Delishies delishies) {
        boolean updated = delishiesService.update(delishies, id);
        return updated ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delishies/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        boolean deleted = delishiesService.delete(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
