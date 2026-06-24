package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.dto.*;
import com.example.FoodIsEasy.model.entity.*;
import com.example.FoodIsEasy.model.entity.repository.*;
import com.example.FoodIsEasy.model.enums.GroupRole;
import com.example.FoodIsEasy.model.enums.VoteStatus;
import com.example.FoodIsEasy.service.GroupVoteService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupVoteServiceImpl implements GroupVoteService {

    private final GroupVoteRepo voteRepo;
    private final GroupVoteBallotRepo ballotRepo;
    private final GroupRepo groupRepo;
    private final GroupMemberRepo groupMemberRepo;
    private final DelishiesRepo delishiesRepo;
    private final UserRepo userRepo;

    public GroupVoteServiceImpl(
            GroupVoteRepo voteRepo,
            GroupVoteBallotRepo ballotRepo,
            GroupRepo groupRepo,
            GroupMemberRepo groupMemberRepo,
            DelishiesRepo delishiesRepo,
            UserRepo userRepo) {
        this.voteRepo = voteRepo;
        this.ballotRepo = ballotRepo;
        this.groupRepo = groupRepo;
        this.groupMemberRepo = groupMemberRepo;
        this.delishiesRepo = delishiesRepo;
        this.userRepo = userRepo;
    }

    @Override
    @Transactional
    public VoteDto createVote(Long userId, Long groupId, CreateVoteRequest request) {
        assertMember(userId, groupId);
        if (request.delishiesIds() == null || request.delishiesIds().size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нужно минимум 2 варианта блюда");
        }

        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Группа не найдена"));
        User creator = userRepo.findById(userId).orElseThrow();

        GroupVote vote = new GroupVote();
        vote.setGroup(group);
        vote.setTitle(request.title() != null ? request.title() : "Выбор блюда");
        vote.setStatus(VoteStatus.OPEN);
        vote.setCreatedBy(creator);
        vote.setCreatedAt(LocalDateTime.now());

        for (Long dishId : request.delishiesIds()) {
            Delishies dish = delishiesRepo.findById(dishId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Блюдо не найдено: " + dishId));
            GroupVoteOption option = new GroupVoteOption();
            option.setVote(vote);
            option.setDelishies(dish);
            vote.getOptions().add(option);
        }

        voteRepo.save(vote);
        return toDto(voteRepo.findByIdWithOptions(vote.getId()).orElseThrow(), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoteDto> listVotes(Long userId, Long groupId) {
        assertMember(userId, groupId);
        return voteRepo.findByGroupIdOrderByCreatedAtDesc(groupId).stream()
                .map(v -> toDto(voteRepo.findByIdWithOptions(v.getId()).orElse(v), userId))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VoteDto getVote(Long userId, Long voteId) {
        GroupVote vote = voteRepo.findByIdWithOptions(voteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Голосование не найдено"));
        assertMember(userId, vote.getGroup().getId());
        return toDto(vote, userId);
    }

    @Override
    @Transactional
    public VoteDto castBallot(Long userId, Long voteId, CastVoteRequest request) {
        GroupVote vote = voteRepo.findByIdWithOptions(voteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Голосование не найдено"));
        assertMember(userId, vote.getGroup().getId());

        if (vote.getStatus() != VoteStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Голосование закрыто");
        }
        if (ballotRepo.existsByVoteIdAndUserId(voteId, userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вы уже проголосовали");
        }

        GroupVoteOption option = vote.getOptions().stream()
                .filter(o -> o.getId().equals(request.optionId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вариант не найден"));

        User user = userRepo.findById(userId).orElseThrow();
        GroupVoteBallot ballot = new GroupVoteBallot();
        ballot.setVote(vote);
        ballot.setUser(user);
        ballot.setOption(option);
        ballot.setVotedAt(LocalDateTime.now());
        ballotRepo.save(ballot);

        return toDto(voteRepo.findByIdWithOptions(voteId).orElseThrow(), userId);
    }

    @Override
    @Transactional
    public VoteDto closeVote(Long userId, Long voteId) {
        GroupVote vote = voteRepo.findByIdWithOptions(voteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Голосование не найдено"));
        assertMember(userId, vote.getGroup().getId());
        if (vote.getStatus() != VoteStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Голосование уже закрыто");
        }
        boolean canClose = vote.getCreatedBy().getId().equals(userId)
                || groupMemberRepo.findByUserId(userId).stream()
                .anyMatch(m -> m.getGroup().getId().equals(vote.getGroup().getId())
                        && m.getRole() == GroupRole.ADMIN);
        if (!canClose) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Закрыть голосование может создатель или администратор");
        }
        vote.setStatus(VoteStatus.CLOSED);
        voteRepo.save(vote);
        return toDto(voteRepo.findByIdWithOptions(voteId).orElseThrow(), userId);
    }

    private void assertMember(Long userId, Long groupId) {
        boolean member = groupMemberRepo.findByUserId(userId).stream()
                .anyMatch(gm -> gm.getGroup().getId().equals(groupId));
        if (!member) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы не состоите в группе");
        }
    }

    private VoteDto toDto(GroupVote vote, Long userId) {
        Map<Long, Long> counts = ballotRepo.countByOption(vote.getId()).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        List<VoteOptionDto> options = vote.getOptions().stream()
                .map(o -> new VoteOptionDto(
                        o.getId(),
                        o.getDelishies().getId(),
                        o.getDelishies().getTitle(),
                        o.getDelishies().getImageUrl(),
                        counts.getOrDefault(o.getId(), 0L)))
                .toList();

        VoteOptionDto winner = resolveWinner(options, vote.getStatus());

        return new VoteDto(
                vote.getId(),
                vote.getTitle(),
                vote.getStatus(),
                vote.getGroup().getId(),
                options,
                ballotRepo.existsByVoteIdAndUserId(vote.getId(), userId),
                vote.getCreatedAt(),
                winner != null ? winner.optionId() : null,
                winner != null ? winner.delishiesId() : null,
                winner != null ? winner.delishiesTitle() : null);
    }

    private VoteOptionDto resolveWinner(List<VoteOptionDto> options, VoteStatus status) {
        if (status != VoteStatus.CLOSED || options.isEmpty()) {
            return null;
        }
        return options.stream()
                .max(Comparator.comparingLong(VoteOptionDto::voteCount)
                        .thenComparing(VoteOptionDto::optionId))
                .orElse(null);
    }
}
