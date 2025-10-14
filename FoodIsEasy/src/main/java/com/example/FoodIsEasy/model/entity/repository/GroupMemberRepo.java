package com.example.FoodIsEasy.model.entity.repository;

import com.example.FoodIsEasy.model.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMemberRepo extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByUserId(Long userId);
}


