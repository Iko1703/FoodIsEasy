package com.example.FoodIsEasy.dto;

import com.example.FoodIsEasy.model.enums.Gender;

import java.util.List;

public record ProfileDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        Integer age,
        Gender gender,
        List<GroupSummaryDto> groups
) {
}
