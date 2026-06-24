package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.dto.CreateGroupRequest;
import com.example.FoodIsEasy.dto.GroupDetailDto;
import com.example.FoodIsEasy.dto.GroupSummaryDto;
import com.example.FoodIsEasy.dto.JoinGroupRequest;

import java.util.List;
import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.service.CurrentUserService;
import com.example.FoodIsEasy.service.GroupManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me/groups")
public class GroupManagementController {

    private final CurrentUserService currentUserService;
    private final GroupManagementService groupManagementService;

    public GroupManagementController(
            CurrentUserService currentUserService,
            GroupManagementService groupManagementService) {
        this.currentUserService = currentUserService;
        this.groupManagementService = groupManagementService;
    }

    @GetMapping("/browse")
    public ResponseEntity<List<GroupSummaryDto>> browse() {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(groupManagementService.discoverGroups(user.getId()));
    }

    @PostMapping
    public ResponseEntity<GroupDetailDto> create(@Valid @RequestBody CreateGroupRequest request) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupManagementService.createGroup(user.getId(), request));
    }

    @PostMapping("/join")
    public ResponseEntity<GroupDetailDto> join(@Valid @RequestBody JoinGroupRequest request) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(groupManagementService.joinGroup(user.getId(), request));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailDto> get(@PathVariable Long groupId) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(groupManagementService.getGroup(user.getId(), groupId));
    }
}
