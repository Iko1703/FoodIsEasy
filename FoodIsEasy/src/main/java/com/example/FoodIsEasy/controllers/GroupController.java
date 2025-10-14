package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.model.entity.Group;
import com.example.FoodIsEasy.service.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/groups")
    public ResponseEntity<?> create(@RequestBody Group group) {
        groupService.create(group);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/groups")
    public ResponseEntity<List<Group>> readAll() {
        List<Group> all = groupService.readAll();
        return all != null && !all.isEmpty() ? new ResponseEntity<>(all, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/groups/{id}")
    public ResponseEntity<?> read(@PathVariable("id") long id) {
        Group item = groupService.read(id);
        return item != null ? new ResponseEntity<>(item, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/groups/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long id, @RequestBody Group group) {
        boolean updated = groupService.update(group, id);
        return updated ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/groups/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        boolean deleted = groupService.delete(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}




