package com.example.FoodIsEasy.model.entity.repository;

import com.example.FoodIsEasy.model.entity.GroupVoteBallot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupVoteBallotRepo extends JpaRepository<GroupVoteBallot, Long> {

    boolean existsByVoteIdAndUserId(Long voteId, Long userId);

    @Query("SELECT b.option.id, COUNT(b) FROM GroupVoteBallot b WHERE b.vote.id = :voteId GROUP BY b.option.id")
    List<Object[]> countByOption(@Param("voteId") Long voteId);
}
