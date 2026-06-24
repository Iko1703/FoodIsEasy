package com.example.FoodIsEasy.model.entity.repository;

import com.example.FoodIsEasy.model.entity.Delishies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface DelishiesRepo extends JpaRepository<Delishies, Long> {

    @Query("SELECT DISTINCT d.id FROM Delishies d JOIN d.products dp WHERE dp.product.id IN :productIds")
    List<Long> findIdsWithProducts(@Param("productIds") Collection<Long> productIds);

    @Query("SELECT DISTINCT d FROM Delishies d "
            + "LEFT JOIN FETCH d.cuisine "
            + "LEFT JOIN FETCH d.category "
            + "LEFT JOIN FETCH d.products dp "
            + "LEFT JOIN FETCH dp.product")
    List<Delishies> findAllWithDetails();

    @Query("SELECT d FROM Delishies d LEFT JOIN FETCH d.products dp LEFT JOIN FETCH dp.product WHERE d.id = :id")
    java.util.Optional<Delishies> findByIdWithProducts(@Param("id") Long id);
}



