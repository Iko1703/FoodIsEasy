package com.example.FoodIsEasy.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "favorite_delishies", uniqueConstraints = {
        @UniqueConstraint(name = "uq_fav_user_delishies", columnNames = {"user_id", "delishies_id"})
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FavoriteDelishies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "delishies_id", nullable = false)
    Delishies delishies;

    public FavoriteDelishies() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Delishies getDelishies() {
        return delishies;
    }

    public void setDelishies(Delishies delishies) {
        this.delishies = delishies;
    }
}



