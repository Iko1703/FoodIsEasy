package com.example.FoodIsEasy.model.entity;

import com.example.FoodIsEasy.model.enums.MealRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "delishies")
public class Delishies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String recipe;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnore
    private User author;

    @ManyToOne
    @JoinColumn(name = "cuisine_id")
    private Cuisine cuisine;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private DishCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_role", nullable = false)
    private MealRole mealRole = MealRole.MAIN;

    @Column(name = "cook_time_minutes")
    private Integer cookTimeMinutes;

    @Column(name = "kcal_total")
    private Integer kcalTotal;

    @Column(name = "protein_total")
    private Double proteinTotal;

    @Column(name = "fat_total")
    private Double fatTotal;

    @Column(name = "carb_total")
    private Double carbTotal;

    @Column(name = "avg_rating")
    private Double avgRating;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @OneToMany(mappedBy = "delishies", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("delishies-products")
    private List<DelishiesProduct> products;

    @OneToMany(mappedBy = "delishies", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("delishies-feedbacks")
    private List<Feedback> feedbacks;

    public Delishies() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRecipe() { return recipe; }
    public void setRecipe(String recipe) { this.recipe = recipe; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public Cuisine getCuisine() { return cuisine; }
    public void setCuisine(Cuisine cuisine) { this.cuisine = cuisine; }
    public DishCategory getCategory() { return category; }
    public void setCategory(DishCategory category) { this.category = category; }
    public MealRole getMealRole() { return mealRole; }
    public void setMealRole(MealRole mealRole) { this.mealRole = mealRole; }
    public Integer getCookTimeMinutes() { return cookTimeMinutes; }
    public void setCookTimeMinutes(Integer cookTimeMinutes) { this.cookTimeMinutes = cookTimeMinutes; }
    public Integer getKcalTotal() { return kcalTotal; }
    public void setKcalTotal(Integer kcalTotal) { this.kcalTotal = kcalTotal; }
    public Double getProteinTotal() { return proteinTotal; }
    public void setProteinTotal(Double proteinTotal) { this.proteinTotal = proteinTotal; }
    public Double getFatTotal() { return fatTotal; }
    public void setFatTotal(Double fatTotal) { this.fatTotal = fatTotal; }
    public Double getCarbTotal() { return carbTotal; }
    public void setCarbTotal(Double carbTotal) { this.carbTotal = carbTotal; }
    public Double getAvgRating() { return avgRating; }
    public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    public List<DelishiesProduct> getProducts() { return products; }
    public void setProducts(List<DelishiesProduct> products) { this.products = products; }
    public List<Feedback> getFeedbacks() { return feedbacks; }
    public void setFeedbacks(List<Feedback> feedbacks) { this.feedbacks = feedbacks; }
}
