package com.hosbee.common.repository;

import com.hosbee.common.entity.Project;
import com.hosbee.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    
    Optional<Project> findByProjectCode(String projectCode);
    
    Page<Project> findByStatus(Project.Status status, Pageable pageable);
    
    Page<Project> findByCategory(Project.Category category, Pageable pageable);
    
    Page<Project> findByClient(User client, Pageable pageable);
    
    Page<Project> findByCreatedBy(User createdBy, Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.status = :status AND p.biddingDeadline > :now")
    Page<Project> findActiveBiddingProjects(@Param("status") Project.Status status, @Param("now") LocalDateTime now, Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.title LIKE %:keyword% OR p.description LIKE %:keyword%")
    Page<Project> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.category = :category AND p.status = :status")
    Page<Project> findByCategoryAndStatus(@Param("category") Project.Category category, @Param("status") Project.Status status, Pageable pageable);
    
    long countByStatus(Project.Status status);
    
    long countByCategory(Project.Category category);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.createdAt >= :startDate AND p.createdAt < :endDate")
    long countByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}