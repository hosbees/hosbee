package com.hosbee.common.repository;

import com.hosbee.common.entity.SystemSetting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    
    Optional<SystemSetting> findByCategoryAndSettingKey(String category, String settingKey);
    
    List<SystemSetting> findByCategory(String category);
    
    Page<SystemSetting> findByCategory(String category, Pageable pageable);
    
    List<SystemSetting> findByIsPublic(Boolean isPublic);
    
    Page<SystemSetting> findByIsPublic(Boolean isPublic, Pageable pageable);
    
    @Query("SELECT s FROM SystemSetting s WHERE s.category = :category AND s.isPublic = :isPublic")
    List<SystemSetting> findByCategoryAndIsPublic(@Param("category") String category, @Param("isPublic") Boolean isPublic);
    
    @Query("SELECT s FROM SystemSetting s WHERE s.settingKey LIKE %:keyword% OR s.settingValue LIKE %:keyword% OR s.description LIKE %:keyword%")
    Page<SystemSetting> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT DISTINCT s.category FROM SystemSetting s ORDER BY s.category")
    List<String> findDistinctCategories();
    
    boolean existsByCategoryAndSettingKey(String category, String settingKey);
}