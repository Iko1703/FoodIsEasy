package com.example.FoodIsEasy.dto;

public record GroupMemberDto(
        Long userId,
        String firstName,
        String lastName,
        String email,
        String role
) {
}
