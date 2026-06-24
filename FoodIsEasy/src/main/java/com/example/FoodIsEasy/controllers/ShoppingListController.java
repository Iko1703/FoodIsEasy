package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.dto.MarkOrderedRequest;
import com.example.FoodIsEasy.dto.ShoppingListDto;
import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.service.CurrentUserService;
import com.example.FoodIsEasy.service.ShoppingListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me/shopping-lists")
public class ShoppingListController {

    private final CurrentUserService currentUserService;
    private final ShoppingListService shoppingListService;

    public ShoppingListController(CurrentUserService currentUserService, ShoppingListService shoppingListService) {
        this.currentUserService = currentUserService;
        this.shoppingListService = shoppingListService;
    }

    @PostMapping("/from-meal-plan/{mealPlanId}")
    public ResponseEntity<ShoppingListDto> generate(@PathVariable Long mealPlanId) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(shoppingListService.generateFromMealPlan(user.getId(), mealPlanId));
    }

    @GetMapping("/by-meal-plan/{mealPlanId}")
    public ResponseEntity<ShoppingListDto> byMealPlan(@PathVariable Long mealPlanId) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(shoppingListService.getByMealPlan(user.getId(), mealPlanId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShoppingListDto> get(@PathVariable Long id) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(shoppingListService.getList(user.getId(), id));
    }

    @PatchMapping("/{id}/items/{itemId}")
    public ResponseEntity<ShoppingListDto> toggle(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @RequestParam boolean checked) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(shoppingListService.toggleItem(user.getId(), id, itemId, checked));
    }

    @PostMapping("/{id}/order")
    public ResponseEntity<ShoppingListDto> order(@PathVariable Long id, @RequestBody(required = false) MarkOrderedRequest request) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(shoppingListService.markOrdered(user.getId(), id, request));
    }
}
