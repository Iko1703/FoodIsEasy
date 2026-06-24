package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.dto.FavoriteDto;
import com.example.FoodIsEasy.model.entity.Delishies;
import com.example.FoodIsEasy.model.entity.FavoriteDelishies;
import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.model.entity.repository.DelishiesRepo;
import com.example.FoodIsEasy.model.entity.repository.FavoriteDelishiesRepo;
import com.example.FoodIsEasy.model.entity.repository.UserRepo;
import com.example.FoodIsEasy.service.FavoriteDelishiesService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FavoriteDelishiesServiceImpl implements FavoriteDelishiesService {

    private final FavoriteDelishiesRepo favoriteDelishiesRepo;
    private final UserRepo userRepo;
    private final DelishiesRepo delishiesRepo;

    public FavoriteDelishiesServiceImpl(
            FavoriteDelishiesRepo favoriteDelishiesRepo,
            UserRepo userRepo,
            DelishiesRepo delishiesRepo) {
        this.favoriteDelishiesRepo = favoriteDelishiesRepo;
        this.userRepo = userRepo;
        this.delishiesRepo = delishiesRepo;
    }

    @Override
    public void create(FavoriteDelishies favoriteDelishies) {
        favoriteDelishiesRepo.save(favoriteDelishies);
    }

    @Override
    public List<FavoriteDelishies> readAll() {
        return favoriteDelishiesRepo.findAll();
    }

    @Override
    public FavoriteDelishies read(long id) {
        return favoriteDelishiesRepo.findById(id).orElse(null);
    }

    @Override
    public boolean update(FavoriteDelishies favoriteDelishies, long id) {
        if (favoriteDelishiesRepo.existsById(id)) {
            favoriteDelishies.setId(id);
            favoriteDelishiesRepo.save(favoriteDelishies);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(long id) {
        if (favoriteDelishiesRepo.existsById(id)) {
            favoriteDelishiesRepo.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteDto> listForUser(Long userId) {
        return favoriteDelishiesRepo.findByUserIdWithDelishies(userId).stream()
                .map(f -> toDto(f.getDelishies()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> favoriteIdsForUser(Long userId) {
        return favoriteDelishiesRepo.findByUserId(userId).stream()
                .map(f -> f.getDelishies().getId())
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public FavoriteDto add(Long userId, Long delishiesId) {
        if (favoriteDelishiesRepo.existsByUserIdAndDelishies_Id(userId, delishiesId)) {
            Delishies dish = delishiesRepo.findById(delishiesId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Блюдо не найдено"));
            return toDto(dish);
        }
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        Delishies dish = delishiesRepo.findById(delishiesId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Блюдо не найдено"));
        FavoriteDelishies fav = new FavoriteDelishies();
        fav.setUser(user);
        fav.setDelishies(dish);
        favoriteDelishiesRepo.save(fav);
        return toDto(dish);
    }

    @Override
    @Transactional
    public void remove(Long userId, Long delishiesId) {
        if (!favoriteDelishiesRepo.existsByUserIdAndDelishies_Id(userId, delishiesId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не в избранном");
        }
        favoriteDelishiesRepo.deleteByUserIdAndDelishies_Id(userId, delishiesId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(Long userId, Long delishiesId) {
        return favoriteDelishiesRepo.existsByUserIdAndDelishies_Id(userId, delishiesId);
    }

    private FavoriteDto toDto(Delishies dish) {
        return new FavoriteDto(
                dish.getId(),
                dish.getTitle(),
                dish.getImageUrl(),
                dish.getKcalTotal(),
                dish.getAvgRating());
    }
}
