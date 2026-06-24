package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.model.entity.repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrentUserService {

    private final UserRepo userRepo;

    public CurrentUserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User requireCurrentUser() {
        User user = getCurrentUserOrNull();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Требуется авторизация");
        }
        return user;
    }

    public User getCurrentUserOrNull() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userRepo.findByEmail(auth.getName()).orElse(null);
    }
}
