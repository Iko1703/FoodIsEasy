package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.dto.FavoriteDto;
import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.service.CurrentUserService;
import com.example.FoodIsEasy.service.FavoriteDelishiesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/me/favorites")
public class MeFavoritesController {

    private final CurrentUserService currentUserService;
    private final FavoriteDelishiesService favoriteDelishiesService;

    public MeFavoritesController(CurrentUserService currentUserService, FavoriteDelishiesService favoriteDelishiesService) {
        this.currentUserService = currentUserService;
        this.favoriteDelishiesService = favoriteDelishiesService;
    }

    @GetMapping
    public ResponseEntity<List<FavoriteDto>> list() {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(favoriteDelishiesService.listForUser(user.getId()));
    }

    @GetMapping("/ids")
    public ResponseEntity<Set<Long>> ids() {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(favoriteDelishiesService.favoriteIdsForUser(user.getId()));
    }

    @PostMapping
    public ResponseEntity<FavoriteDto> add(@RequestBody Map<String, Long> body) {
        User user = currentUserService.requireCurrentUser();
        Long delishiesId = body.get("delishiesId");
        if (delishiesId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favoriteDelishiesService.add(user.getId(), delishiesId));
    }

    @DeleteMapping("/{delishiesId}")
    public ResponseEntity<Void> remove(@PathVariable Long delishiesId) {
        User user = currentUserService.requireCurrentUser();
        favoriteDelishiesService.remove(user.getId(), delishiesId);
        return ResponseEntity.noContent().build();
    }
}
