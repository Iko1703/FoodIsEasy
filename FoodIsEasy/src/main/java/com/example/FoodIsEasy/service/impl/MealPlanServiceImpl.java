package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.dto.*;
import com.example.FoodIsEasy.model.entity.*;
import com.example.FoodIsEasy.model.entity.repository.*;
import com.example.FoodIsEasy.model.enums.MealPlanScope;
import com.example.FoodIsEasy.model.enums.MealRole;
import com.example.FoodIsEasy.model.enums.MealType;
import com.example.FoodIsEasy.service.MealPlanService;
import com.example.FoodIsEasy.service.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MealPlanServiceImpl implements MealPlanService {

    private static final MealType[] DAY_MEALS = {MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER};

    private final MealPlanRepo mealPlanRepo;
    private final UserRepo userRepo;
    private final GroupRepo groupRepo;
    private final GroupMemberRepo groupMemberRepo;
    private final DelishiesRepo delishiesRepo;
    private final RecommendationService recommendationService;

    public MealPlanServiceImpl(
            MealPlanRepo mealPlanRepo,
            UserRepo userRepo,
            GroupRepo groupRepo,
            GroupMemberRepo groupMemberRepo,
            DelishiesRepo delishiesRepo,
            RecommendationService recommendationService) {
        this.mealPlanRepo = mealPlanRepo;
        this.userRepo = userRepo;
        this.groupRepo = groupRepo;
        this.groupMemberRepo = groupMemberRepo;
        this.delishiesRepo = delishiesRepo;
        this.recommendationService = recommendationService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MealPlanSummaryDto> listPersonalPlans(Long userId) {
        return mealPlanRepo.findByUserIdOrderByStartDateDesc(userId).stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MealPlanSummaryDto> listGroupPlans(Long userId, Long groupId) {
        assertGroupMember(userId, groupId);
        return mealPlanRepo.findByGroupIdOrderByStartDateDesc(groupId).stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MealPlanDetailDto getPlan(Long userId, Long planId) {
        MealPlan plan = loadPlanWithAccess(userId, planId);
        return toDetail(plan);
    }

    @Override
    @Transactional
    public MealPlanDetailDto createPlan(Long userId, CreateMealPlanRequest request) {
        if (request.startDate() == null || request.endDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Укажите даты плана");
        }
        if (request.endDate().isBefore(request.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата окончания раньше начала");
        }

        MealPlan plan = new MealPlan();
        plan.setName(request.name() != null && !request.name().isBlank()
                ? request.name()
                : "Меню " + request.startDate() + " — " + request.endDate());
        plan.setStartDate(request.startDate());
        plan.setEndDate(request.endDate());
        plan.setCreatedAt(LocalDateTime.now());

        if (request.groupId() != null) {
            assertGroupMember(userId, request.groupId());
            Group group = groupRepo.findById(request.groupId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Группа не найдена"));
            plan.setGroup(group);
            plan.setScope(MealPlanScope.GROUP);
        } else {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
            plan.setUser(user);
            plan.setScope(MealPlanScope.PERSONAL);
        }

        mealPlanRepo.save(plan);
        return getPlan(userId, plan.getId());
    }

    @Override
    @Transactional
    public MealPlanDetailDto setEntry(Long userId, Long planId, SetMealPlanEntryRequest request) {
        MealPlan plan = loadPlanWithAccess(userId, planId);
        validateDateInPlan(plan, request.planDate());

        Delishies dish = delishiesRepo.findById(request.delishiesId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Блюдо не найдено"));

        boolean duplicate = plan.getEntries().stream()
                .anyMatch(e -> e.getPlanDate().equals(request.planDate())
                        && e.getMealType() == request.mealType()
                        && e.getDelishies().getId().equals(dish.getId()));
        if (duplicate) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Это блюдо уже добавлено на данный приём пищи");
        }

        if (dish.getMealRole() != null) {
            boolean roleTaken = plan.getEntries().stream()
                    .filter(e -> e.getPlanDate().equals(request.planDate()) && e.getMealType() == request.mealType())
                    .map(e -> e.getDelishies().getMealRole())
                    .anyMatch(role -> role == dish.getMealRole());
            if (roleTaken) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "В этом приёме пищи уже есть блюдо с ролью «" + roleLabel(dish.getMealRole()) + "»");
            }
        }

        MealPlanEntry entry = new MealPlanEntry();
        entry.setMealPlan(plan);
        entry.setPlanDate(request.planDate());
        entry.setMealType(request.mealType());
        entry.setDelishies(dish);
        plan.getEntries().add(entry);
        mealPlanRepo.save(plan);
        return getPlan(userId, planId);
    }

    @Override
    @Transactional
    public void removeEntry(Long userId, Long planId, Long entryId) {
        MealPlan plan = loadPlanWithAccess(userId, planId);
        boolean removed = plan.getEntries().removeIf(e -> e.getId().equals(entryId));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Слот меню не найден");
        }
        mealPlanRepo.save(plan);
    }

    @Override
    @Transactional
    public MealPlanDetailDto generatePlan(Long userId, Long planId) {
        MealPlan plan = loadPlanWithAccess(userId, planId);
        int days = (int) plan.getStartDate().datesUntil(plan.getEndDate().plusDays(1)).count();
        int slots = days * DAY_MEALS.length;
        int limit = Math.max(slots * 3, 30);

        List<RecommendationDto> ranked;
        if (plan.getScope() == MealPlanScope.GROUP && plan.getGroup() != null) {
            ranked = recommendationService.getGroupRecommendations(
                    userId, plan.getGroup().getId(), limit);
        } else {
            ranked = recommendationService.getPersonalRecommendations(userId, limit);
        }

        if (ranked.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нет подходящих блюд для генерации");
        }

        Set<Long> usedInPlan = new HashSet<>();
        int rankIndex = 0;

        plan.getEntries().clear();
        Map<Long, MealRole> dishRoles = loadDishRoles(ranked);

        for (LocalDate date = plan.getStartDate(); !date.isAfter(plan.getEndDate()); date = date.plusDays(1)) {
            final LocalDate planDate = date;
            for (MealType mealType : DAY_MEALS) {
                if (mealType == MealType.LUNCH) {
                    for (MealRole role : List.of(MealRole.SOUP, MealRole.MAIN, MealRole.SALAD)) {
                        Long dishId = pickByRole(ranked, dishRoles, usedInPlan, role);
                        if (dishId == null) continue;
                        addPlanEntry(plan, planDate, mealType, dishId, usedInPlan);
                    }
                    if (plan.getEntries().stream().noneMatch(e -> e.getPlanDate().equals(planDate) && e.getMealType() == MealType.LUNCH)) {
                        Long fallback = pickRecommendedDishId(ranked, usedInPlan, rankIndex);
                        rankIndex = nextRankIndex(ranked, rankIndex);
                        addPlanEntry(plan, planDate, mealType, fallback, usedInPlan);
                    }
                } else {
                    MealRole preferred = mealType == MealType.BREAKFAST ? MealRole.BREAKFAST : MealRole.MAIN;
                    Long dishId = pickByRole(ranked, dishRoles, usedInPlan, preferred);
                    if (dishId == null) {
                        dishId = pickRecommendedDishId(ranked, usedInPlan, rankIndex);
                        rankIndex = nextRankIndex(ranked, rankIndex);
                    }
                    addPlanEntry(plan, planDate, mealType, dishId, usedInPlan);
                }
            }
        }
        mealPlanRepo.save(plan);
        return getPlan(userId, planId);
    }

    @Override
    @Transactional
    public void deletePlan(Long userId, Long planId) {
        MealPlan plan = loadPlanWithAccess(userId, planId);
        mealPlanRepo.delete(plan);
    }

    private void addPlanEntry(MealPlan plan, LocalDate date, MealType mealType, Long dishId, Set<Long> usedInPlan) {
        Delishies dish = delishiesRepo.findById(dishId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Блюдо не найдено"));
        usedInPlan.add(dishId);
        MealPlanEntry entry = new MealPlanEntry();
        entry.setMealPlan(plan);
        entry.setPlanDate(date);
        entry.setMealType(mealType);
        entry.setDelishies(dish);
        plan.getEntries().add(entry);
    }

    private Map<Long, MealRole> loadDishRoles(List<RecommendationDto> ranked) {
        Map<Long, MealRole> roles = new HashMap<>();
        for (RecommendationDto r : ranked) {
            delishiesRepo.findById(r.delishiesId()).ifPresent(d -> roles.put(d.getId(), d.getMealRole()));
        }
        return roles;
    }

    private Long pickByRole(List<RecommendationDto> ranked, Map<Long, MealRole> roles,
                            Set<Long> usedInPlan, MealRole role) {
        for (RecommendationDto r : ranked) {
            Long id = r.delishiesId();
            if (roles.get(id) == role && !usedInPlan.contains(id)) {
                return id;
            }
        }
        return null;
    }

    private static String roleLabel(MealRole role) {
        return switch (role) {
            case BREAKFAST -> "Завтрак";
            case SOUP -> "Суп";
            case MAIN -> "Основное";
            case SALAD -> "Салат";
            case SIDE -> "Гарнир";
            case DESSERT -> "Десерт";
            case SNACK -> "Перекус";
        };
    }

    private Long pickRecommendedDishId(List<RecommendationDto> ranked, Set<Long> usedInPlan, int startIndex) {
        for (int i = 0; i < ranked.size(); i++) {
            Long id = ranked.get((startIndex + i) % ranked.size()).delishiesId();
            if (!usedInPlan.contains(id)) {
                return id;
            }
        }
        return ranked.get(startIndex % ranked.size()).delishiesId();
    }

    private int nextRankIndex(List<RecommendationDto> ranked, int current) {
        return (current + 1) % ranked.size();
    }

    private MealPlan loadPlanWithAccess(Long userId, Long planId) {
        MealPlan plan = mealPlanRepo.findByIdWithEntries(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "План не найден"));
        if (plan.getUser() != null && plan.getUser().getId().equals(userId)) {
            return plan;
        }
        if (plan.getGroup() != null) {
            assertGroupMember(userId, plan.getGroup().getId());
            return plan;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа к плану");
    }

    private void assertGroupMember(Long userId, Long groupId) {
        boolean member = groupMemberRepo.findByUserId(userId).stream()
                .anyMatch(gm -> gm.getGroup().getId().equals(groupId));
        if (!member) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы не состоите в этой группе");
        }
    }

    private void validateDateInPlan(MealPlan plan, LocalDate date) {
        if (date.isBefore(plan.getStartDate()) || date.isAfter(plan.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата вне диапазона плана");
        }
    }

    private MealPlanSummaryDto toSummary(MealPlan plan) {
        return new MealPlanSummaryDto(
                plan.getId(),
                plan.getName(),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.getScope(),
                plan.getGroup() != null ? plan.getGroup().getId() : null,
                plan.getGroup() != null ? plan.getGroup().getName() : null,
                plan.getEntries() != null ? plan.getEntries().size() : 0);
    }

    private MealPlanDetailDto toDetail(MealPlan plan) {
        List<MealPlanEntryDto> entries = plan.getEntries() == null ? List.of() : plan.getEntries().stream()
                .sorted(Comparator.comparing(MealPlanEntry::getPlanDate)
                        .thenComparing(MealPlanEntry::getMealType)
                        .thenComparing(MealPlanEntry::getId, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(e -> new MealPlanEntryDto(
                        e.getId(),
                        e.getPlanDate(),
                        e.getMealType(),
                        e.getDelishies().getId(),
                        e.getDelishies().getTitle(),
                        e.getDelishies().getImageUrl(),
                        e.getDelishies().getMealRole()))
                .toList();
        return new MealPlanDetailDto(
                plan.getId(),
                plan.getName(),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.getScope(),
                plan.getGroup() != null ? plan.getGroup().getId() : null,
                plan.getGroup() != null ? plan.getGroup().getName() : null,
                entries);
    }
}
