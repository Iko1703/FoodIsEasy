package com.example.FoodIsEasy;

import com.example.FoodIsEasy.dto.RecommendationDto;
import com.example.FoodIsEasy.model.entity.repository.UserRepo;
import com.example.FoodIsEasy.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class RecommendationServiceTest {

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

    @Autowired RecommendationService recommendationService;
    @Autowired UserRepo userRepo;

    @Test
    void personalRecommendationsNotEmpty() {
        var user = userRepo.findByEmail("alice@foodiseasy.ru").orElseThrow();
        List<RecommendationDto> recs = recommendationService.getPersonalRecommendations(user.getId(), 5);
        assertThat(recs).isNotEmpty();
        assertThat(recs.get(0).score()).isGreaterThan(0);
    }
}
