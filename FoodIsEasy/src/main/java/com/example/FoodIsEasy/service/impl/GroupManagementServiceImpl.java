package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.dto.*;
import com.example.FoodIsEasy.model.entity.Group;
import com.example.FoodIsEasy.model.entity.GroupMember;
import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.model.entity.repository.GroupMemberRepo;
import com.example.FoodIsEasy.model.entity.repository.GroupRepo;
import com.example.FoodIsEasy.model.entity.repository.UserRepo;
import com.example.FoodIsEasy.model.enums.GroupRole;
import com.example.FoodIsEasy.service.GroupManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupManagementServiceImpl implements GroupManagementService {

    private final GroupRepo groupRepo;
    private final GroupMemberRepo groupMemberRepo;
    private final UserRepo userRepo;

    public GroupManagementServiceImpl(GroupRepo groupRepo, GroupMemberRepo groupMemberRepo, UserRepo userRepo) {
        this.groupRepo = groupRepo;
        this.groupMemberRepo = groupMemberRepo;
        this.userRepo = userRepo;
    }

    @Override
    @Transactional
    public GroupDetailDto createGroup(Long userId, CreateGroupRequest request) {
        String name = request.name().trim();
        if (name.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Укажите название группы");
        }
        if (groupRepo.existsByName(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Группа с таким названием уже существует");
        }

        User owner = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        Group group = new Group();
        group.setName(name);
        group.setOwner(owner);
        groupRepo.save(group);

        GroupMember membership = new GroupMember();
        membership.setGroup(group);
        membership.setUser(owner);
        membership.setRole(GroupRole.ADMIN);
        groupMemberRepo.save(membership);

        return toDetail(group, userId);
    }

    @Override
    @Transactional
    public GroupDetailDto joinGroup(Long userId, JoinGroupRequest request) {
        if (request.groupId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Укажите ID группы");
        }
        Group group = groupRepo.findById(request.groupId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Группа не найдена"));

        boolean already = groupMemberRepo.findByUserId(userId).stream()
                .anyMatch(m -> m.getGroup().getId().equals(group.getId()));
        if (already) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Вы уже в этой группе");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        GroupMember membership = new GroupMember();
        membership.setGroup(group);
        membership.setUser(user);
        membership.setRole(GroupRole.MEMBER);
        groupMemberRepo.save(membership);

        return toDetail(group, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupSummaryDto> discoverGroups(Long userId) {
        Set<Long> myGroups = groupMemberRepo.findByUserId(userId).stream()
                .map(m -> m.getGroup().getId())
                .collect(Collectors.toSet());

        return groupRepo.findAll().stream()
                .map(g -> {
                    String role = null;
                    if (myGroups.contains(g.getId())) {
                        role = groupMemberRepo.findByGroupId(g.getId()).stream()
                                .filter(m -> m.getUser().getId().equals(userId))
                                .map(m -> m.getRole().name())
                                .findFirst()
                                .orElse("MEMBER");
                    }
                    return new GroupSummaryDto(g.getId(), g.getName(), role);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GroupDetailDto getGroup(Long userId, Long groupId) {
        assertMember(userId, groupId);
        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Группа не найдена"));
        return toDetail(group, userId);
    }

    private GroupDetailDto toDetail(Group group, Long userId) {
        List<GroupMemberDto> members = groupMemberRepo.findByGroupId(group.getId()).stream()
                .map(m -> new GroupMemberDto(
                        m.getUser().getId(),
                        m.getUser().getFirstName(),
                        m.getUser().getLastName(),
                        m.getUser().getEmail(),
                        m.getRole().name()))
                .toList();

        String myRole = members.stream()
                .filter(m -> m.userId().equals(userId))
                .map(GroupMemberDto::role)
                .findFirst()
                .orElse("MEMBER");

        User owner = group.getOwner();
        String ownerName = owner.getFirstName() != null
                ? owner.getFirstName() + (owner.getLastName() != null ? " " + owner.getLastName() : "")
                : owner.getEmail();

        return new GroupDetailDto(
                group.getId(),
                group.getName(),
                owner.getId(),
                ownerName.trim(),
                myRole,
                members);
    }

    private void assertMember(Long userId, Long groupId) {
        boolean member = groupMemberRepo.findByUserId(userId).stream()
                .anyMatch(m -> m.getGroup().getId().equals(groupId));
        if (!member) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы не состоите в этой группе");
        }
    }
}
