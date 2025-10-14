package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.model.entity.Delishies;
import com.example.FoodIsEasy.service.DelishiesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DelishiesController {
    private final DelishiesService delishiesService;

    public DelishiesController(DelishiesService delishiesService) {
        this.delishiesService = delishiesService;
    }

    @PostMapping("/delishies")
    public ResponseEntity<?> create(@RequestBody Delishies delishies) {
        delishiesService.create(delishies);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/delishies")
    public ResponseEntity<List<Delishies>> readAll() {
        List<Delishies> all = delishiesService.readAll();
        return all != null && !all.isEmpty() ? new ResponseEntity<>(all, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/delishies/{id}")
    public ResponseEntity<?> read(@PathVariable("id") long id) {
        Delishies item = delishiesService.read(id);
        return item != null ? new ResponseEntity<>(item, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/delishies/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long id, @RequestBody Delishies delishies) {
        boolean updated = delishiesService.update(delishies, id);
        return updated ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delishies/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        boolean deleted = delishiesService.delete(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}




