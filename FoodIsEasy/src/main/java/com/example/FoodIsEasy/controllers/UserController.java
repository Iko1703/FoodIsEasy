package com.example.FoodIsEasy.controllers;


import com.example.FoodIsEasy.service.UserService;
import com.example.FoodIsEasy.model.entity.User;
// removed lombok.RequiredArgsConstructor

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// removed @RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final com.example.FoodIsEasy.model.entity.repository.GroupRepo groupRepo;
    private final com.example.FoodIsEasy.model.entity.repository.GroupMemberRepo groupMemberRepo;
    private final com.example.FoodIsEasy.model.entity.repository.UserRepo userRepo;

    public UserController(UserService userService, com.example.FoodIsEasy.model.entity.repository.GroupRepo groupRepo, com.example.FoodIsEasy.model.entity.repository.GroupMemberRepo groupMemberRepo, com.example.FoodIsEasy.model.entity.repository.UserRepo userRepo) {
        this.userService = userService;
        this.groupRepo = groupRepo;
        this.groupMemberRepo = groupMemberRepo;
        this.userRepo = userRepo;
    }

    @PostMapping(value = "/users")
    public ResponseEntity<?> createUser(@RequestBody User user){
        userService.createUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> readAllUser() {
        final List<User> users = userService.readAllUser();
        return users != null && !users.isEmpty()
                ? new ResponseEntity<>(users, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @GetMapping(value = "/users/{id}")
    public ResponseEntity<?> readUser(@PathVariable(name = "id") long id) {
        final User user = userService.readUser(id);
        return user != null
                ? new ResponseEntity<>(user, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PutMapping(value = "/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable(name = "id") long id, @RequestBody User user) {
        final boolean updated = userService.updateUser(user, id);
        return updated
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }
    @DeleteMapping(value = "/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "id") long id) {
        final boolean deleted = userService.deleteUser(id);
        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @GetMapping(value = "/users/age/{age}")
    public ResponseEntity<List<User>> read(@PathVariable(name = "age") int age) {
        final List<User> users = userService.readAllUserByAge(age);

        return users != null && !users.isEmpty()
                ? new ResponseEntity<>(users, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        if (principal == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        User user = userRepo.findByEmail(principal.getUsername()).orElse(null);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/me/groups")
    public ResponseEntity<?> getMyGroups(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        if (principal == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        User user = userRepo.findByEmail(principal.getUsername()).orElse(null);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        java.util.List<com.example.FoodIsEasy.model.entity.GroupMember> memberships = groupMemberRepo.findByUserId(user.getId());
        return new ResponseEntity<>(memberships, HttpStatus.OK);
    }
}
