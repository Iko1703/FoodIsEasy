package com.example.FoodIsEasy.model.entity;

import com.example.FoodIsEasy.model.enums.MealType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "meal_history")
public class MealHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "delishies_id", nullable = false)
    private Delishies delishies;

    @Column(name = "eaten_at", nullable = false)
    private LocalDateTime eatenAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Delishies getDelishies() { return delishies; }
    public void setDelishies(Delishies delishies) { this.delishies = delishies; }
    public LocalDateTime getEatenAt() { return eatenAt; }
    public void setEatenAt(LocalDateTime eatenAt) { this.eatenAt = eatenAt; }
    public MealType getMealType() { return mealType; }
    public void setMealType(MealType mealType) { this.mealType = mealType; }
}
