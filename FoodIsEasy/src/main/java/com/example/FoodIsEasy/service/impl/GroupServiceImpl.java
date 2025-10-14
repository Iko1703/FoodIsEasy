package com.example.FoodIsEasy.service.impl;

import com.example.FoodIsEasy.model.entity.Group;
import com.example.FoodIsEasy.model.entity.repository.GroupRepo;
import com.example.FoodIsEasy.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepo groupRepo;

    @Override
    public void create(Group group) {
        groupRepo.save(group);
    }

    @Override
    public List<Group> readAll() {
        return groupRepo.findAll();
    }

    @Override
    public Group read(long id) {
        return groupRepo.findById(id).orElse(null);
    }

    @Override
    public boolean update(Group group, long id) {
        if (groupRepo.existsById(id)) {
            group.setId(id);
            groupRepo.save(group);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(long id) {
        if (groupRepo.existsById(id)) {
            groupRepo.deleteById(id);
            return true;
        }
        return false;
    }
}




