package com.example.FoodIsEasy.model.entity.repository;

import com.example.FoodIsEasy.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepo extends JpaRepository <User,Long> {
    @Query(value = "SELECT * FROM users WHERE age > :age", nativeQuery = true)
    List<User> findUsersByAgeGreaterThan(@Param("age") int age);

    java.util.Optional<User> findByEmail(String email);
}
