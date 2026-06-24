package com.example.FoodIsEasy.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateGroupRequest(
        @NotBlank String name
) {
}
