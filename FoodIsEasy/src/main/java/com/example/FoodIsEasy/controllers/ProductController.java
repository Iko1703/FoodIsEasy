package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.model.entity.Product;
import com.example.FoodIsEasy.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products")
    public ResponseEntity<?> create(@RequestBody Product product) {
        productService.create(product);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> readAll() {
        List<Product> products = productService.readAll();
        return products != null && !products.isEmpty() ? new ResponseEntity<>(products, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> read(@PathVariable("id") long id) {
        Product product = productService.read(id);
        return product != null ? new ResponseEntity<>(product, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long id, @RequestBody Product product) {
        boolean updated = productService.update(product, id);
        return updated ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        boolean deleted = productService.delete(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}




