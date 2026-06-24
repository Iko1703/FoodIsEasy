package com.example.FoodIsEasy.model.entity.repository;

import com.example.FoodIsEasy.model.entity.FavoriteDelishies;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteDelishiesRepo extends JpaRepository<FavoriteDelishies, Long> {
    java.util.List<FavoriteDelishies> findByUserId(Long userId);

    @org.springframework.data.jpa.repository.Query(
            "SELECT f FROM FavoriteDelishies f JOIN FETCH f.delishies WHERE f.user.id = :userId")
    java.util.List<FavoriteDelishies> findByUserIdWithDelishies(
            @org.springframework.data.repository.query.Param("userId") Long userId);

    boolean existsByUserIdAndDelishies_Id(Long userId, Long delishiesId);

    void deleteByUserIdAndDelishies_Id(Long userId, Long delishiesId);
}



