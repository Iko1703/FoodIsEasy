package com.example.FoodIsEasy.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_cuisine_preferences")
public class UserCuisinePreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cuisine_id", nullable = false)
    private Cuisine cuisine;

    @Column(nullable = false)
    private Integer weight = 1;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Cuisine getCuisine() { return cuisine; }
    public void setCuisine(Cuisine cuisine) { this.cuisine = cuisine; }
    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }
}
