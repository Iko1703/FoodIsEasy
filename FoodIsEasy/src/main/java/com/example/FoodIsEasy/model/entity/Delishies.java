package com.example.FoodIsEasy.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "delishies")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Delishies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String title;

    @Column(length = 2000)
    String description;

    @Column(name = "image_url")
    String imageUrl;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    User author;

    @Column(name = "created_at")
    LocalDate createdAt;

    @OneToMany(mappedBy = "delishies", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("delishies-products")
    List<DelishiesProduct> products;

    @OneToMany(mappedBy = "delishies", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("delishies-feedbacks")
    List<Feedback> feedbacks;

    public Delishies() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public List<DelishiesProduct> getProducts() {
        return products;
    }

    public void setProducts(List<DelishiesProduct> products) {
        this.products = products;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }
}



