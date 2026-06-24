package com.example.FoodIsEasy.dto;

import java.util.List;

public record GroupDetailDto(
        Long id,
        String name,
        Long ownerId,
        String ownerName,
        String myRole,
        List<GroupMemberDto> members
) {
}
