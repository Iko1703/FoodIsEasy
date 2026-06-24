package com.example.FoodIsEasy.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFeedbackRequest(
        @NotBlank @Size(max = 2000) String message,
        @Min(1) @Max(5) Short rating
) {
}
