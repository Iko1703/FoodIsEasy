package com.example.FoodIsEasy;

import com.example.FoodIsEasy.model.entity.Delishies;
import com.example.FoodIsEasy.model.entity.repository.DelishiesRepo;
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
class DelishiesRepositoryTest {

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

    @Autowired
    DelishiesRepo delishiesRepo;

    @Test
    void seedDataContainsDishes() {
        List<Delishies> all = delishiesRepo.findAll();
        assertThat(all).isNotEmpty();
        assertThat(all.get(0).getTitle()).isNotBlank();
    }
}
