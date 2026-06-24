package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.dto.RecommendationDto;
import com.example.FoodIsEasy.model.entity.*;
import com.example.FoodIsEasy.model.entity.repository.*;
import com.example.FoodIsEasy.model.enums.ProductPreferenceType;
import com.example.FoodIsEasy.service.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final DelishiesRepo delishiesRepo;
    private final UserCuisinePreferenceRepo cuisinePrefRepo;
    private final UserProductPreferenceRepo productPrefRepo;
    private final MealHistoryRepo mealHistoryRepo;
    private final FavoriteDelishiesRepo favoriteDelishiesRepo;
    private final GroupMemberRepo groupMemberRepo;

    public RecommendationServiceImpl(
            DelishiesRepo delishiesRepo,
            UserCuisinePreferenceRepo cuisinePrefRepo,
            UserProductPreferenceRepo productPrefRepo,
            MealHistoryRepo mealHistoryRepo,
            FavoriteDelishiesRepo favoriteDelishiesRepo,
            GroupMemberRepo groupMemberRepo) {
        this.delishiesRepo = delishiesRepo;
        this.cuisinePrefRepo = cuisinePrefRepo;
        this.productPrefRepo = productPrefRepo;
        this.mealHistoryRepo = mealHistoryRepo;
        this.favoriteDelishiesRepo = favoriteDelishiesRepo;
        this.groupMemberRepo = groupMemberRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationDto> getPersonalRecommendations(Long userId, int limit) {
        UserPrefs prefs = UserPrefs.load(userId, cuisinePrefRepo, productPrefRepo, mealHistoryRepo, favoriteDelishiesRepo);
        return scoreAndRank(delishiesRepo.findAllWithDetails(), prefs, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationDto> getGroupRecommendations(Long userId, Long groupId, int limit) {
        boolean member = groupMemberRepo.findByUserId(userId).stream()
                .anyMatch(gm -> gm.getGroup().getId().equals(groupId));
        if (!member) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы не состоите в этой группе");
        }

        List<Long> memberIds = groupMemberRepo.findByGroupId(groupId).stream()
                .map(gm -> gm.getUser().getId())
                .toList();

        List<UserPrefs> allPrefs = memberIds.stream()
                .map(id -> UserPrefs.load(id, cuisinePrefRepo, productPrefRepo, mealHistoryRepo, favoriteDelishiesRepo))
                .toList();

        List<Delishies> dishes = delishiesRepo.findAllWithDetails();
        List<ScoredDish> scored = new ArrayList<>();

        for (Delishies dish : dishes) {
            if (hasAllergyConflict(dish, allPrefs)) continue;

            List<ScoreResult> memberScores = allPrefs.stream()
                    .map(p -> scoreDish(dish, p))
                    .filter(r -> r.score > 0)
                    .toList();

            if (memberScores.isEmpty()) continue;

            double avg = memberScores.stream().mapToDouble(r -> r.score).average().orElse(0);
            Set<String> reasons = new LinkedHashSet<>();
            reasons.add("Подходит участникам группы");
            memberScores.stream().flatMap(r -> r.reasons.stream()).limit(3).forEach(reasons::add);

            scored.add(new ScoredDish(dish, avg, new ArrayList<>(reasons)));
        }

        return scored.stream()
                .sorted(Comparator.comparingDouble((ScoredDish s) -> s.score).reversed())
                .limit(Math.max(1, Math.min(limit, 200)))
                .map(s -> toDto(s.dish, s.score, s.reasons))
                .toList();
    }

    private List<RecommendationDto> scoreAndRank(List<Delishies> dishes, UserPrefs prefs, int limit) {
        return dishes.stream()
                .map(d -> scoreDish(d, prefs))
                .filter(r -> r.score > 0)
                .sorted(Comparator.comparingDouble((ScoreResult r) -> r.score).reversed())
                .limit(Math.max(1, Math.min(limit, 200)))
                .map(r -> toDto(r.dish, r.score, r.reasons))
                .toList();
    }

    private boolean hasAllergyConflict(Delishies dish, List<UserPrefs> prefsList) {
        return prefsList.stream().anyMatch(p -> scoreDish(dish, p).score <= 0);
    }

    private ScoreResult scoreDish(Delishies dish, UserPrefs prefs) {
        List<String> reasons = new ArrayList<>();
        double score = 40.0;

        if (dish.getAvgRating() != null) {
            score += dish.getAvgRating() * 8;
            if (dish.getAvgRating() >= 4.5) reasons.add("Высокий рейтинг");
        }

        Set<Long> dishProductIds = dish.getProducts() == null ? Set.of() : dish.getProducts().stream()
                .map(dp -> dp.getProduct().getId())
                .collect(Collectors.toSet());

        for (Long pid : dishProductIds) {
            ProductPreferenceType type = prefs.productPrefs.get(pid);
            if (type == ProductPreferenceType.ALLERGY) {
                return new ScoreResult(dish, 0, List.of("Содержит аллерген"));
            }
            if (type == ProductPreferenceType.FAVORITE) {
                score += 6;
                reasons.add("Содержит любимый продукт");
            }
            if (type == ProductPreferenceType.DISLIKED) {
                score -= 12;
            }
            if (type == ProductPreferenceType.SOFT_DISLIKE) {
                score -= 4;
            }
        }

        if (dish.getCuisine() != null) {
            Integer weight = prefs.cuisineWeights.get(dish.getCuisine().getId());
            if (weight != null) {
                score += weight * 5;
                reasons.add("Любимая кухня: " + dish.getCuisine().getName());
            }
        }

        if (prefs.favoriteDishIds.contains(dish.getId())) {
            score += 12;
            reasons.add("В избранном");
        }

        if (prefs.recent14.contains(dish.getId())) {
            score -= 20;
            reasons.add("Недавно уже ели");
        } else if (prefs.recent30.contains(dish.getId())) {
            score -= 8;
        } else {
            score += 5;
            reasons.add("Добавит разнообразие");
        }

        if (score < 1) {
            return new ScoreResult(dish, 0, reasons);
        }
        return new ScoreResult(dish, score, reasons.stream().distinct().limit(4).toList());
    }

    private RecommendationDto toDto(Delishies dish, double score, List<String> reasons) {
        return new RecommendationDto(
                dish.getId(),
                dish.getTitle(),
                dish.getDescription(),
                dish.getImageUrl(),
                dish.getCookTimeMinutes(),
                dish.getKcalTotal(),
                Math.round(score * 10.0) / 10.0,
                reasons);
    }

    private record ScoreResult(Delishies dish, double score, List<String> reasons) {}
    private record ScoredDish(Delishies dish, double score, List<String> reasons) {}

    private static final class UserPrefs {
        final Map<Long, Integer> cuisineWeights;
        final Map<Long, ProductPreferenceType> productPrefs;
        final Set<Long> recent14;
        final Set<Long> recent30;
        final Set<Long> favoriteDishIds;

        UserPrefs(Map<Long, Integer> cuisineWeights, Map<Long, ProductPreferenceType> productPrefs,
                  Set<Long> recent14, Set<Long> recent30, Set<Long> favoriteDishIds) {
            this.cuisineWeights = cuisineWeights;
            this.productPrefs = productPrefs;
            this.recent14 = recent14;
            this.recent30 = recent30;
            this.favoriteDishIds = favoriteDishIds;
        }

        static UserPrefs load(Long userId,
                              UserCuisinePreferenceRepo cuisinePrefRepo,
                              UserProductPreferenceRepo productPrefRepo,
                              MealHistoryRepo mealHistoryRepo,
                              FavoriteDelishiesRepo favoriteRepo) {
            Map<Long, Integer> cuisines = cuisinePrefRepo.findByUser_Id(userId).stream()
                    .collect(Collectors.toMap(p -> p.getCuisine().getId(), UserCuisinePreference::getWeight));
            Map<Long, ProductPreferenceType> products = productPrefRepo.findByUser_Id(userId).stream()
                    .collect(Collectors.toMap(p -> p.getProduct().getId(), UserProductPreference::getPrefType, (a, b) -> a));
            Set<Long> r14 = new HashSet<>(mealHistoryRepo.findRecentDishIds(userId, LocalDateTime.now().minusDays(14)));
            Set<Long> r30 = new HashSet<>(mealHistoryRepo.findRecentDishIds(userId, LocalDateTime.now().minusDays(30)));
            Set<Long> favs = favoriteRepo.findByUserId(userId).stream()
                    .map(f -> f.getDelishies().getId())
                    .collect(Collectors.toSet());
            return new UserPrefs(cuisines, products, r14, r30, favs);
        }
    }
}
