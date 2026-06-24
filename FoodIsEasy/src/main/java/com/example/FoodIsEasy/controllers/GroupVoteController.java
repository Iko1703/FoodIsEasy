package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.dto.CastVoteRequest;
import com.example.FoodIsEasy.dto.CreateVoteRequest;
import com.example.FoodIsEasy.dto.VoteDto;
import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.service.CurrentUserService;
import com.example.FoodIsEasy.service.GroupVoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups/{groupId}/votes")
public class GroupVoteController {

    private final CurrentUserService currentUserService;
    private final GroupVoteService groupVoteService;

    public GroupVoteController(CurrentUserService currentUserService, GroupVoteService groupVoteService) {
        this.currentUserService = currentUserService;
        this.groupVoteService = groupVoteService;
    }

    @GetMapping
    public ResponseEntity<List<VoteDto>> list(@PathVariable Long groupId) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(groupVoteService.listVotes(user.getId(), groupId));
    }

    @PostMapping
    public ResponseEntity<VoteDto> create(@PathVariable Long groupId, @Valid @RequestBody CreateVoteRequest request) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(groupVoteService.createVote(user.getId(), groupId, request));
    }

    @GetMapping("/{voteId}")
    public ResponseEntity<VoteDto> get(@PathVariable Long groupId, @PathVariable Long voteId) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(groupVoteService.getVote(user.getId(), voteId));
    }

    @PostMapping("/{voteId}/ballot")
    public ResponseEntity<VoteDto> vote(@PathVariable Long groupId, @PathVariable Long voteId, @Valid @RequestBody CastVoteRequest request) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(groupVoteService.castBallot(user.getId(), voteId, request));
    }

    @PostMapping("/{voteId}/close")
    public ResponseEntity<VoteDto> close(@PathVariable Long groupId, @PathVariable Long voteId) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(groupVoteService.closeVote(user.getId(), voteId));
    }
}
