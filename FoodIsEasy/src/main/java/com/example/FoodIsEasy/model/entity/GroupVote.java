package com.example.FoodIsEasy.model.entity;

import com.example.FoodIsEasy.model.enums.VoteStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "group_votes")
public class GroupVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteStatus status = VoteStatus.OPEN;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupVoteOption> options = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public VoteStatus getStatus() { return status; }
    public void setStatus(VoteStatus status) { this.status = status; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getEndsAt() { return endsAt; }
    public void setEndsAt(LocalDateTime endsAt) { this.endsAt = endsAt; }
    public List<GroupVoteOption> getOptions() { return options; }
    public void setOptions(List<GroupVoteOption> options) { this.options = options; }
}
