package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.dto.*;
import com.example.FoodIsEasy.model.entity.*;
import com.example.FoodIsEasy.model.entity.repository.*;
import com.example.FoodIsEasy.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepo userRepo;
    private final GroupMemberRepo groupMemberRepo;
    private final UserCuisinePreferenceRepo cuisinePrefRepo;
    private final UserProductPreferenceRepo productPrefRepo;
    private final CuisineRepo cuisineRepo;
    private final ProductRepo productRepo;

    public ProfileServiceImpl(
            UserRepo userRepo,
            GroupMemberRepo groupMemberRepo,
            UserCuisinePreferenceRepo cuisinePrefRepo,
            UserProductPreferenceRepo productPrefRepo,
            CuisineRepo cuisineRepo,
            ProductRepo productRepo) {
        this.userRepo = userRepo;
        this.groupMemberRepo = groupMemberRepo;
        this.cuisinePrefRepo = cuisinePrefRepo;
        this.productPrefRepo = productPrefRepo;
        this.cuisineRepo = cuisineRepo;
        this.productRepo = productRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileDto getProfile(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        List<GroupSummaryDto> groups = groupMemberRepo.findByUserId(userId).stream()
                .map(gm -> new GroupSummaryDto(
                        gm.getGroup().getId(),
                        gm.getGroup().getName(),
                        gm.getRole().name()))
                .toList();
        return new ProfileDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getGender(),
                groups);
    }

    @Override
    @Transactional
    public ProfileDto updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.age() != null) user.setAge(request.age());
        if (request.gender() != null) user.setGender(request.gender());
        userRepo.save(user);
        return getProfile(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public PreferencesDto getPreferences(Long userId) {
        List<CuisinePrefDto> cuisines = cuisinePrefRepo.findByUser_Id(userId).stream()
                .map(p -> new CuisinePrefDto(
                        p.getCuisine().getId(),
                        p.getCuisine().getName(),
                        p.getWeight()))
                .toList();
        List<ProductPrefDto> products = productPrefRepo.findByUser_Id(userId).stream()
                .map(p -> new ProductPrefDto(
                        p.getProduct().getId(),
                        p.getProduct().getName(),
                        p.getPrefType()))
                .toList();
        return new PreferencesDto(cuisines, products);
    }

    @Override
    @Transactional
    public PreferencesDto savePreferences(Long userId, SavePreferencesRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        cuisinePrefRepo.deleteByUser_Id(userId);
        if (request.cuisines() != null) {
            Set<Long> seenCuisines = new HashSet<>();
            for (SavePreferencesRequest.CuisinePrefInput input : request.cuisines()) {
                if (input.cuisineId() == null || !seenCuisines.add(input.cuisineId())) continue;
                Cuisine cuisine = cuisineRepo.findById(input.cuisineId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Кухня не найдена: " + input.cuisineId()));
                UserCuisinePreference pref = new UserCuisinePreference();
                pref.setUser(user);
                pref.setCuisine(cuisine);
                pref.setWeight(input.weight() != null ? Math.max(1, Math.min(5, input.weight())) : 1);
                cuisinePrefRepo.save(pref);
            }
        }

        productPrefRepo.deleteByUser_Id(userId);
        if (request.products() != null) {
            Set<String> seenProducts = new HashSet<>();
            for (SavePreferencesRequest.ProductPrefInput input : request.products()) {
                if (input.productId() == null || input.prefType() == null) continue;
                String key = input.productId() + ":" + input.prefType();
                if (!seenProducts.add(key)) continue;
                Product product = productRepo.findById(input.productId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Продукт не найден: " + input.productId()));
                UserProductPreference pref = new UserProductPreference();
                pref.setUser(user);
                pref.setProduct(product);
                pref.setPrefType(input.prefType());
                productPrefRepo.save(pref);
            }
        }

        return getPreferences(userId);
    }
}
