package com.example.FoodIsEasy.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    User author;

    @ManyToOne(optional = false)
    @JoinColumn(name = "delishies_id", nullable = false)
    @JsonBackReference("delishies-feedbacks")
    Delishies delishies;

    @Column(nullable = false, length = 2000)
    String message;

    Short rating;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    public Feedback() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Delishies getDelishies() {
        return delishies;
    }

    public void setDelishies(Delishies delishies) {
        this.delishies = delishies;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Short getRating() {
        return rating;
    }

    public void setRating(Short rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}



