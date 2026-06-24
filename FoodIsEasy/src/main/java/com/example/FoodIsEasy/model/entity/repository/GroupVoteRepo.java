package com.example.FoodIsEasy.model.entity.repository;

import com.example.FoodIsEasy.model.entity.GroupVote;
import com.example.FoodIsEasy.model.enums.VoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupVoteRepo extends JpaRepository<GroupVote, Long> {

    List<GroupVote> findByGroupIdAndStatusOrderByCreatedAtDesc(Long groupId, VoteStatus status);

    List<GroupVote> findByGroupIdOrderByCreatedAtDesc(Long groupId);

    @Query("SELECT DISTINCT v FROM GroupVote v LEFT JOIN FETCH v.options o LEFT JOIN FETCH o.delishies WHERE v.id = :id")
    Optional<GroupVote> findByIdWithOptions(@Param("id") Long id);
}
