package com.example.FoodIsEasy;

import com.example.FoodIsEasy.dto.PreferencesDto;
import com.example.FoodIsEasy.dto.SavePreferencesRequest;
import com.example.FoodIsEasy.model.entity.repository.CuisineRepo;
import com.example.FoodIsEasy.model.entity.repository.ProductRepo;
import com.example.FoodIsEasy.model.entity.repository.UserRepo;
import com.example.FoodIsEasy.model.enums.ProductPreferenceType;
import com.example.FoodIsEasy.service.ProfileService;
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
class ProfileServiceTest {

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
    ProfileService profileService;
    @Autowired
    UserRepo userRepo;
    @Autowired
    CuisineRepo cuisineRepo;
    @Autowired
    ProductRepo productRepo;

    @Test
    void saveAndLoadPreferences() {
        var user = userRepo.findByEmail("alice@foodiseasy.ru").orElseThrow();
        var cuisine = cuisineRepo.findAll().get(0);
        var product = productRepo.findAll().get(0);

        PreferencesDto saved = profileService.savePreferences(user.getId(), new SavePreferencesRequest(
                List.of(new SavePreferencesRequest.CuisinePrefInput(cuisine.getId(), 4)),
                List.of(new SavePreferencesRequest.ProductPrefInput(product.getId(), ProductPreferenceType.FAVORITE))
        ));

        assertThat(saved.cuisines()).hasSize(1);
        assertThat(saved.cuisines().get(0).weight()).isEqualTo(4);
        assertThat(saved.products()).hasSize(1);
        assertThat(saved.products().get(0).prefType()).isEqualTo(ProductPreferenceType.FAVORITE);

        PreferencesDto loaded = profileService.getPreferences(user.getId());
        assertThat(loaded.cuisines()).hasSize(1);
        assertThat(loaded.products()).hasSize(1);
    }

    @Test
    void getProfileIncludesGroups() {
        var user = userRepo.findByEmail("alice@foodiseasy.ru").orElseThrow();
        var profile = profileService.getProfile(user.getId());
        assertThat(profile.email()).isEqualTo("alice@foodiseasy.ru");
        assertThat(profile.groups()).isNotEmpty();
    }
}
