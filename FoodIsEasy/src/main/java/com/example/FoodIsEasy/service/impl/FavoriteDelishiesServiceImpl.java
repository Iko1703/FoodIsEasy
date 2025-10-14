package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.model.entity.FavoriteDelishies;
import com.example.FoodIsEasy.model.entity.repository.FavoriteDelishiesRepo;
import com.example.FoodIsEasy.service.FavoriteDelishiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteDelishiesServiceImpl implements FavoriteDelishiesService {

    private final FavoriteDelishiesRepo favoriteDelishiesRepo;

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
}




