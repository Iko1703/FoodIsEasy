package com.example.FoodIsEasy.model.entity.repository;

import com.example.FoodIsEasy.model.entity.UserProductPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserProductPreferenceRepo extends JpaRepository<UserProductPreference, Long> {
    List<UserProductPreference> findByUser_Id(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM UserProductPreference p WHERE p.user.id = :userId")
    void deleteByUser_Id(@Param("userId") Long userId);
}
