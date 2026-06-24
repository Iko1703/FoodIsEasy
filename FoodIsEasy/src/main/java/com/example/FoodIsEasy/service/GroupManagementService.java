package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.dto.CreateGroupRequest;
import com.example.FoodIsEasy.dto.GroupDetailDto;
import com.example.FoodIsEasy.dto.GroupSummaryDto;
import com.example.FoodIsEasy.dto.JoinGroupRequest;

import java.util.List;

public interface GroupManagementService {
    GroupDetailDto createGroup(Long userId, CreateGroupRequest request);
    GroupDetailDto joinGroup(Long userId, JoinGroupRequest request);
    GroupDetailDto getGroup(Long userId, Long groupId);
    List<GroupSummaryDto> discoverGroups(Long userId);
}
