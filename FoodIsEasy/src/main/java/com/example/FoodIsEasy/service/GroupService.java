package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.model.entity.Group;

import java.util.List;

public interface GroupService {
    void create(Group group);
    List<Group> readAll();
    Group read(long id);
    boolean update(Group group, long id);
    boolean delete(long id);
}




