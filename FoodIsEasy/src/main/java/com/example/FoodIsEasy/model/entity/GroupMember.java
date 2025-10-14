package com.example.FoodIsEasy.model.entity;

import com.example.FoodIsEasy.model.enums.GroupRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "group_members", uniqueConstraints = {
        @UniqueConstraint(name = "uq_group_user", columnNames = {"group_id", "user_id"})
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    Group group;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    GroupRole role;

    public GroupMember() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GroupRole getRole() {
        return role;
    }

    public void setRole(GroupRole role) {
        this.role = role;
    }
}


