package com.example.FoodIsEasy.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "shopping_list_items")
public class ShoppingListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shopping_list_id", nullable = false)
    private ShoppingList shoppingList;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity_grams", nullable = false)
    private Integer quantityGrams;

    @Column(nullable = false)
    private boolean checked = false;

    @Column(name = "custom_name")
    private String customName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ShoppingList getShoppingList() { return shoppingList; }
    public void setShoppingList(ShoppingList shoppingList) { this.shoppingList = shoppingList; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getQuantityGrams() { return quantityGrams; }
    public void setQuantityGrams(Integer quantityGrams) { this.quantityGrams = quantityGrams; }
    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
    public String getCustomName() { return customName; }
    public void setCustomName(String customName) { this.customName = customName; }
}
