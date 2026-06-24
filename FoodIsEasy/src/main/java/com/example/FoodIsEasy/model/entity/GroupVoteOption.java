package com.example.FoodIsEasy.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "group_vote_options")
public class GroupVoteOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vote_id", nullable = false)
    private GroupVote vote;

    @ManyToOne(optional = false)
    @JoinColumn(name = "delishies_id", nullable = false)
    private Delishies delishies;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GroupVote getVote() { return vote; }
    public void setVote(GroupVote vote) { this.vote = vote; }
    public Delishies getDelishies() { return delishies; }
    public void setDelishies(Delishies delishies) { this.delishies = delishies; }
}
