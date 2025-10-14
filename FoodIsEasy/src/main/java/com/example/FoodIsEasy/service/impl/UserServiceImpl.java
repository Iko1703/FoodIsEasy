package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.model.entity.repository.UserRepo;
import com.example.FoodIsEasy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    //private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepo userRepo;

    @Override
    public void createUser(User user){
        userRepo.save(user);
        log.info("Создан новый user с кодом: " +String.valueOf(user.getId()));
    }

    @Override
    public List<User> readAllUser() {
        return userRepo.findAll();
    }

    @Override
    public User readUser(long id) {
        return userRepo.findById(id).get();
    }

    @Override
    public boolean updateUser(User user, long id) {
        if (userRepo.existsById(id)){
            user.setId(id);
            userRepo.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteUser(long id) {
        if (userRepo.existsById(id)){
            userRepo.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<User> readAllUserByAge(int age){
        return userRepo.findUsersByAgeGreaterThan(age);
    }
}