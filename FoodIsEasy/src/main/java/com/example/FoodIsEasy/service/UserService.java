package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.model.entity.User;

import java.util.List;

public interface UserService {

    void createUser(User user);

    List<User> readAllUser();

    User readUser(long id);

    boolean updateUser(User user, long id);

    boolean deleteUser(long id);

    List<User> readAllUserByAge(int age);
}