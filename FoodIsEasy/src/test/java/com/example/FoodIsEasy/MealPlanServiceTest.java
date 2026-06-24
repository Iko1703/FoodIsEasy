package com.example.FoodIsEasy;

import com.example.FoodIsEasy.dto.CreateMealPlanRequest;
import com.example.FoodIsEasy.dto.MealPlanDetailDto;
import com.example.FoodIsEasy.dto.SetMealPlanEntryRequest;
import com.example.FoodIsEasy.model.entity.repository.UserRepo;
import com.example.FoodIsEasy.service.MealHistoryService;
import com.example.FoodIsEasy.service.MealPlanService;
import com.example.FoodIsEasy.dto.LogMealRequest;
import com.example.FoodIsEasy.model.enums.MealType;
import com.example.FoodIsEasy.model.entity.repository.DelishiesRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class MealPlanServiceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("foodiseasy_test")
            .withUsername("food")
            .withPassword("food");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired MealPlanService mealPlanService;
    @Autowired MealHistoryService mealHistoryService;
    @Autowired UserRepo userRepo;
    @Autowired DelishiesRepo delishiesRepo;

    @Test
    void createAndGenerateMealPlan() {
        var user = userRepo.findByEmail("alice@foodiseasy.ru").orElseThrow();
        var dish = delishiesRepo.findAll().get(0);

        mealHistoryService.logMeal(user.getId(), new LogMealRequest(dish.getId(), MealType.LUNCH, null));

        LocalDate start = LocalDate.now();
        MealPlanDetailDto created = mealPlanService.createPlan(user.getId(), new CreateMealPlanRequest(
                "Тестовая неделя", start, start.plusDays(2), null));

        assertThat(created.id()).isNotNull();
        assertThat(created.entries()).isEmpty();

        MealPlanDetailDto generated = mealPlanService.generatePlan(user.getId(), created.id());
        assertThat(generated.entries().size()).isGreaterThanOrEqualTo(9);
    }

    @Test
    void multipleDishesPerMealSlot() {
        var user = userRepo.findByEmail("alice@foodiseasy.ru").orElseThrow();
        var dishes = delishiesRepo.findAll();
        assertThat(dishes.size()).isGreaterThanOrEqualTo(2);

        LocalDate day = LocalDate.now();
        MealPlanDetailDto plan = mealPlanService.createPlan(user.getId(), new CreateMealPlanRequest(
                "Мульти-слот", day, day, null));

        mealPlanService.setEntry(user.getId(), plan.id(), new SetMealPlanEntryRequest(
                day, MealType.LUNCH, dishes.get(0).getId()));
        MealPlanDetailDto withTwo = mealPlanService.setEntry(user.getId(), plan.id(), new SetMealPlanEntryRequest(
                day, MealType.LUNCH, dishes.get(1).getId()));

        assertThat(withTwo.entries()).hasSize(2);
        assertThat(withTwo.entries().stream().filter(e -> e.mealType() == MealType.LUNCH).count()).isEqualTo(2);
    }
}
