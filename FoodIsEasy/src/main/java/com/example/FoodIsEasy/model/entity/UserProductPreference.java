package com.example.FoodIsEasy.model.entity;

import com.example.FoodIsEasy.model.enums.ProductPreferenceType;
import jakarta.persistence.*;

@Entity
@Table(name = "user_product_preferences")
public class UserProductPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "pref_type", nullable = false)
    private ProductPreferenceType prefType;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public ProductPreferenceType getPrefType() { return prefType; }
    public void setPrefType(ProductPreferenceType prefType) { this.prefType = prefType; }
}
