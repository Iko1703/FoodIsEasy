package com.example.FoodIsEasy.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String name;

    @Column(name = "fat_per_100g")
    Double fatPer100g;

    @Column(name = "protein_per_100g")
    Double proteinPer100g;

    @Column(name = "carb_per_100g")
    Double carbPer100g;

    @Column(name = "kcal_per_100g")
    Integer kcalPer100g;

    @ManyToOne
    @JoinColumn(name = "category_id")
    ProductCategory category;


    public Product() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getFatPer100g() {
        return fatPer100g;
    }

    public void setFatPer100g(Double fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    public Double getCarbPer100g() {
        return carbPer100g;
    }

    public void setCarbPer100g(Double carbPer100g) {
        this.carbPer100g = carbPer100g;
    }

    public Double getProteinPer100g() {
        return proteinPer100g;
    }

    public void setProteinPer100g(Double proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    public Integer getKcalPer100g() {
        return kcalPer100g;
    }

    public void setKcalPer100g(Integer kcalPer100g) {
        this.kcalPer100g = kcalPer100g;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }
}



