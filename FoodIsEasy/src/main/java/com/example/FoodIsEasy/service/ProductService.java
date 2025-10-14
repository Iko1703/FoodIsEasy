package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.model.entity.Product;

import java.util.List;

public interface ProductService {
    void create(Product product);
    List<Product> readAll();
    Product read(long id);
    boolean update(Product product, long id);
    boolean delete(long id);
}



