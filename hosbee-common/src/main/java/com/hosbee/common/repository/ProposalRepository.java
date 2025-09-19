package com.hosbee.common.repository;

import com.hosbee.common.entity.Proposal;
import com.hosbee.common.entity.Project;
import com.hosbee.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long>, JpaSpecificationExecutor<Proposal> {
    
    Optional<Proposal> findByProposalCode(String proposalCode);
    
    Page<Proposal> findByProject(Project project, Pageable pageable);
    
    Page<Proposal> findByProposer(User proposer, Pageable pageable);
    
    Page<Proposal> findByStatus(Proposal.Status status, Pageable pageable);
    
    @Query("SELECT p FROM Proposal p WHERE p.project = :project AND p.status = :status")
    List<Proposal> findByProjectAndStatus(@Param("project") Project project, @Param("status") Proposal.Status status);
    
    @Query("SELECT p FROM Proposal p WHERE p.proposer = :proposer AND p.status = :status")
    Page<Proposal> findByProposerAndStatus(@Param("proposer") User proposer, @Param("status") Proposal.Status status, Pageable pageable);
    
    @Query("SELECT p FROM Proposal p WHERE p.project = :project ORDER BY p.price ASC")
    List<Proposal> findByProjectOrderByPriceAsc(@Param("project") Project project);
    
    @Query("SELECT p FROM Proposal p WHERE p.project = :project AND p.price BETWEEN :minPrice AND :maxPrice")
    List<Proposal> findByProjectAndPriceRange(@Param("project") Project project, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    boolean existsByProjectAndProposer(Project project, User proposer);
    
    long countByProject(Project project);
    
    long countByProposer(User proposer);
    
    long countByStatus(Proposal.Status status);
    
    @Query("SELECT COUNT(p) FROM Proposal p WHERE p.submittedAt >= :startDate AND p.submittedAt < :endDate")
    long countBySubmissionDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}