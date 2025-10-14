package com.example.FoodIsEasy.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "delishies_products", uniqueConstraints = {
        @UniqueConstraint(name = "uq_delishies_product", columnNames = {"delishies_id", "product_id"})
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DelishiesProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "delishies_id", nullable = false)
    @JsonBackReference("delishies-products")
    Delishies delishies;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @Column(name = "quantity_grams", nullable = false)
    Integer quantityGrams;

    public DelishiesProduct() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Delishies getDelishies() {
        return delishies;
    }

    public void setDelishies(Delishies delishies) {
        this.delishies = delishies;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantityGrams() {
        return quantityGrams;
    }

    public void setQuantityGrams(Integer quantityGrams) {
        this.quantityGrams = quantityGrams;
    }
}



