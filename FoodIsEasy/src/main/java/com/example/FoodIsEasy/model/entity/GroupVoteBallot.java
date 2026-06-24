package com.example.FoodIsEasy.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_vote_ballots")
public class GroupVoteBallot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vote_id", nullable = false)
    private GroupVote vote;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "option_id", nullable = false)
    private GroupVoteOption option;

    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GroupVote getVote() { return vote; }
    public void setVote(GroupVote vote) { this.vote = vote; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public GroupVoteOption getOption() { return option; }
    public void setOption(GroupVoteOption option) { this.option = option; }
    public LocalDateTime getVotedAt() { return votedAt; }
    public void setVotedAt(LocalDateTime votedAt) { this.votedAt = votedAt; }
}
