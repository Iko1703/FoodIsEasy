package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.dto.*;

public interface ProfileService {
    ProfileDto getProfile(Long userId);
    ProfileDto updateProfile(Long userId, UpdateProfileRequest request);
    PreferencesDto getPreferences(Long userId);
    PreferencesDto savePreferences(Long userId, SavePreferencesRequest request);
}
