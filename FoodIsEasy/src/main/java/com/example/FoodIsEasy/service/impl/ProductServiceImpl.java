package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.model.entity.Product;
import com.example.FoodIsEasy.model.entity.repository.ProductRepo;
import com.example.FoodIsEasy.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;

    @Override
    public void create(Product product) {
        productRepo.save(product);
    }

    @Override
    public List<Product> readAll() {
        return productRepo.findAll();
    }

    @Override
    public Product read(long id) {
        return productRepo.findById(id).orElse(null);
    }

    @Override
    public boolean update(Product product, long id) {
        if (productRepo.existsById(id)) {
            product.setId(id);
            productRepo.save(product);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(long id) {
        if (productRepo.existsById(id)) {
            productRepo.deleteById(id);
            return true;
        }
        return false;
    }
}




