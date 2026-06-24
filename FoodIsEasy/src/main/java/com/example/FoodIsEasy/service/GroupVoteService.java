package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.dto.CastVoteRequest;
import com.example.FoodIsEasy.dto.CreateVoteRequest;
import com.example.FoodIsEasy.dto.VoteDto;

import java.util.List;

public interface GroupVoteService {
    VoteDto createVote(Long userId, Long groupId, CreateVoteRequest request);
    List<VoteDto> listVotes(Long userId, Long groupId);
    VoteDto getVote(Long userId, Long voteId);
    VoteDto castBallot(Long userId, Long voteId, CastVoteRequest request);
    VoteDto closeVote(Long userId, Long voteId);
}
