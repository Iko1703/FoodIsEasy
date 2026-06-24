package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.dto.MarkOrderedRequest;
import com.example.FoodIsEasy.dto.ShoppingListDto;
import com.example.FoodIsEasy.dto.ShoppingListItemDto;
import com.example.FoodIsEasy.model.entity.*;
import com.example.FoodIsEasy.model.entity.repository.GroupMemberRepo;
import com.example.FoodIsEasy.model.entity.repository.MealPlanRepo;
import com.example.FoodIsEasy.model.entity.repository.ShoppingListRepo;
import com.example.FoodIsEasy.model.enums.ShoppingListStatus;
import com.example.FoodIsEasy.service.ShoppingListService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {

    private final ShoppingListRepo shoppingListRepo;
    private final MealPlanRepo mealPlanRepo;
    private final GroupMemberRepo groupMemberRepo;

    public ShoppingListServiceImpl(
            ShoppingListRepo shoppingListRepo,
            MealPlanRepo mealPlanRepo,
            GroupMemberRepo groupMemberRepo) {
        this.shoppingListRepo = shoppingListRepo;
        this.mealPlanRepo = mealPlanRepo;
        this.groupMemberRepo = groupMemberRepo;
    }

    @Override
    @Transactional
    public ShoppingListDto generateFromMealPlan(Long userId, Long mealPlanId) {
        MealPlan plan = mealPlanRepo.findByIdWithEntries(mealPlanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "План не найден"));
        assertPlanAccess(userId, plan);

        ShoppingList list = shoppingListRepo.findByMealPlanId(mealPlanId).orElseGet(() -> {
            ShoppingList s = new ShoppingList();
            s.setMealPlan(plan);
            s.setGroup(plan.getGroup());
            s.setUser(plan.getUser());
            s.setStatus(ShoppingListStatus.ACTIVE);
            s.setCreatedAt(LocalDateTime.now());
            return s;
        });

        list.getItems().clear();
        Map<Long, Integer> productGrams = new HashMap<>();
        Map<Long, Product> products = new HashMap<>();

        for (MealPlanEntry entry : plan.getEntries()) {
            Delishies dish = entry.getDelishies();
            if (dish.getProducts() == null) continue;
            for (DelishiesProduct dp : dish.getProducts()) {
                Long pid = dp.getProduct().getId();
                productGrams.merge(pid, dp.getQuantityGrams(), Integer::sum);
                products.putIfAbsent(pid, dp.getProduct());
            }
        }

        for (Map.Entry<Long, Integer> e : productGrams.entrySet()) {
            ShoppingListItem item = new ShoppingListItem();
            item.setShoppingList(list);
            item.setProduct(products.get(e.getKey()));
            item.setQuantityGrams(e.getValue());
            item.setChecked(false);
            list.getItems().add(item);
        }

        list.setUpdatedAt(LocalDateTime.now());
        shoppingListRepo.save(list);
        return toDto(shoppingListRepo.findByIdWithItems(list.getId()).orElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingListDto getList(Long userId, Long listId) {
        ShoppingList list = shoppingListRepo.findByIdWithItems(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Список не найден"));
        assertListAccess(userId, list);
        return toDto(list);
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingListDto getByMealPlan(Long userId, Long mealPlanId) {
        MealPlan plan = mealPlanRepo.findById(mealPlanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "План не найден"));
        assertPlanAccess(userId, plan);
        ShoppingList list = shoppingListRepo.findByMealPlanId(mealPlanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Корзина для плана ещё не создана"));
        return toDto(shoppingListRepo.findByIdWithItems(list.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public ShoppingListDto toggleItem(Long userId, Long listId, Long itemId, boolean checked) {
        ShoppingList list = shoppingListRepo.findByIdWithItems(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Список не найден"));
        assertListAccess(userId, list);
        list.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Позиция не найдена"))
                .setChecked(checked);
        list.setUpdatedAt(LocalDateTime.now());
        shoppingListRepo.save(list);
        return toDto(list);
    }

    @Override
    @Transactional
    public ShoppingListDto markOrdered(Long userId, Long listId, MarkOrderedRequest request) {
        ShoppingList list = shoppingListRepo.findByIdWithItems(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Список не найден"));
        assertListAccess(userId, list);
        list.setStatus(ShoppingListStatus.ORDERED);
        list.setOrderNote(request != null && request.orderNote() != null
                ? request.orderNote()
                : "Заявка отправлена в магазин (демо)");
        list.setOrderedAt(LocalDateTime.now());
        list.setUpdatedAt(LocalDateTime.now());
        shoppingListRepo.save(list);
        return toDto(list);
    }

    private void assertPlanAccess(Long userId, MealPlan plan) {
        if (plan.getUser() != null && plan.getUser().getId().equals(userId)) return;
        if (plan.getGroup() != null) {
            boolean member = groupMemberRepo.findByUserId(userId).stream()
                    .anyMatch(gm -> gm.getGroup().getId().equals(plan.getGroup().getId()));
            if (member) return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа");
    }

    private void assertListAccess(Long userId, ShoppingList list) {
        if (list.getMealPlan() != null) {
            assertPlanAccess(userId, list.getMealPlan());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа");
        }
    }

    private ShoppingListDto toDto(ShoppingList list) {
        List<ShoppingListItemDto> items = list.getItems().stream()
                .map(i -> new ShoppingListItemDto(
                        i.getId(),
                        i.getProduct().getId(),
                        i.getProduct().getName(),
                        i.getQuantityGrams(),
                        i.isChecked(),
                        i.getCustomName()))
                .toList();
        MealPlan plan = list.getMealPlan();
        return new ShoppingListDto(
                list.getId(),
                plan != null ? plan.getId() : null,
                plan != null ? plan.getName() : null,
                list.getGroup() != null ? list.getGroup().getId() : null,
                list.getGroup() != null ? list.getGroup().getName() : null,
                list.getStatus(),
                list.getOrderNote(),
                list.getOrderedAt(),
                items);
    }
}
