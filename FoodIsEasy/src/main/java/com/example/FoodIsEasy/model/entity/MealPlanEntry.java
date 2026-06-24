package com.example.FoodIsEasy.model.entity;

import com.example.FoodIsEasy.model.enums.MealType;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "meal_plan_entries")
public class MealPlanEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;

    @Column(name = "plan_date", nullable = false)
    private LocalDate planDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "delishies_id", nullable = false)
    private Delishies delishies;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public MealPlan getMealPlan() { return mealPlan; }
    public void setMealPlan(MealPlan mealPlan) { this.mealPlan = mealPlan; }
    public LocalDate getPlanDate() { return planDate; }
    public void setPlanDate(LocalDate planDate) { this.planDate = planDate; }
    public MealType getMealType() { return mealType; }
    public void setMealType(MealType mealType) { this.mealType = mealType; }
    public Delishies getDelishies() { return delishies; }
    public void setDelishies(Delishies delishies) { this.delishies = delishies; }
}
