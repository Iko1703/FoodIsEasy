package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.model.entity.Delishies;
import com.example.FoodIsEasy.model.entity.repository.DelishiesRepo;
import com.example.FoodIsEasy.service.DelishiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DelishiesServiceImpl implements DelishiesService {

    private final DelishiesRepo delishiesRepo;

    @Override
    public void create(Delishies delishies) {
        delishiesRepo.save(delishies);
    }

    @Override
    public List<Delishies> readAll() {
        return delishiesRepo.findAll();
    }

    @Override
    public Delishies read(long id) {
        return delishiesRepo.findById(id).orElse(null);
    }

    @Override
    public boolean update(Delishies delishies, long id) {
        if (delishiesRepo.existsById(id)) {
            delishies.setId(id);
            delishiesRepo.save(delishies);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(long id) {
        if (delishiesRepo.existsById(id)) {
            delishiesRepo.deleteById(id);
            return true;
        }
        return false;
    }
}




