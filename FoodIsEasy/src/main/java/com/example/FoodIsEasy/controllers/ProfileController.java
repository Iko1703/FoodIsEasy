package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.dto.PreferencesDto;
import com.example.FoodIsEasy.dto.ProfileDto;
import com.example.FoodIsEasy.dto.SavePreferencesRequest;
import com.example.FoodIsEasy.dto.UpdateProfileRequest;
import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.service.CurrentUserService;
import com.example.FoodIsEasy.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
public class ProfileController {

    private final CurrentUserService currentUserService;
    private final ProfileService profileService;

    public ProfileController(CurrentUserService currentUserService, ProfileService profileService) {
        this.currentUserService = currentUserService;
        this.profileService = profileService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getProfile() {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(profileService.getProfile(user.getId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileDto> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(profileService.updateProfile(user.getId(), request));
    }

    @GetMapping("/preferences")
    public ResponseEntity<PreferencesDto> getPreferences() {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(profileService.getPreferences(user.getId()));
    }

    @PutMapping("/preferences")
    public ResponseEntity<PreferencesDto> savePreferences(@Valid @RequestBody SavePreferencesRequest request) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(profileService.savePreferences(user.getId(), request));
    }
}
