package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.model.entity.FavoriteDelishies;
import com.example.FoodIsEasy.service.FavoriteDelishiesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FavoriteDelishiesController {
    private final FavoriteDelishiesService favoriteDelishiesService;

    public FavoriteDelishiesController(FavoriteDelishiesService favoriteDelishiesService) {
        this.favoriteDelishiesService = favoriteDelishiesService;
    }

    @PostMapping("/favorite-delishies")
    public ResponseEntity<?> create(@RequestBody FavoriteDelishies favoriteDelishies) {
        favoriteDelishiesService.create(favoriteDelishies);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/favorite-delishies")
    public ResponseEntity<List<FavoriteDelishies>> readAll() {
        List<FavoriteDelishies> all = favoriteDelishiesService.readAll();
        return all != null && !all.isEmpty() ? new ResponseEntity<>(all, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/favorite-delishies/{id}")
    public ResponseEntity<?> read(@PathVariable("id") long id) {
        FavoriteDelishies item = favoriteDelishiesService.read(id);
        return item != null ? new ResponseEntity<>(item, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/favorite-delishies/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long id, @RequestBody FavoriteDelishies favoriteDelishies) {
        boolean updated = favoriteDelishiesService.update(favoriteDelishies, id);
        return updated ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/favorite-delishies/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        boolean deleted = favoriteDelishiesService.delete(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}




