package com.hosbee.common.repository;

import com.hosbee.common.entity.Contract;
import com.hosbee.common.entity.Project;
import com.hosbee.common.entity.Proposal;
import com.hosbee.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    
    Optional<Contract> findByContractCode(String contractCode);
    
    Optional<Contract> findByProject(Project project);
    
    Optional<Contract> findByProposal(Proposal proposal);
    
    Page<Contract> findByClient(User client, Pageable pageable);
    
    Page<Contract> findByContractor(User contractor, Pageable pageable);
    
    Page<Contract> findByStatus(Contract.Status status, Pageable pageable);
    
    @Query("SELECT c FROM Contract c WHERE c.client = :client AND c.status = :status")
    Page<Contract> findByClientAndStatus(@Param("client") User client, @Param("status") Contract.Status status, Pageable pageable);
    
    @Query("SELECT c FROM Contract c WHERE c.contractor = :contractor AND c.status = :status")
    Page<Contract> findByContractorAndStatus(@Param("contractor") User contractor, @Param("status") Contract.Status status, Pageable pageable);
    
    @Query("SELECT c FROM Contract c WHERE (c.client = :user OR c.contractor = :user)")
    Page<Contract> findByUser(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT c FROM Contract c WHERE c.endDate < :currentDate AND c.status = 'ACTIVE'")
    List<Contract> findExpiredContracts(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT c FROM Contract c WHERE c.startDate <= :currentDate AND c.endDate >= :currentDate AND c.status = 'ACTIVE'")
    List<Contract> findActiveContractsOnDate(@Param("currentDate") LocalDate currentDate);
    
    long countByStatus(Contract.Status status);
    
    long countByClient(User client);
    
    long countByContractor(User contractor);
    
    @Query("SELECT COUNT(c) FROM Contract c WHERE c.signedAt >= :startDate AND c.signedAt < :endDate")
    long countBySignedDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}