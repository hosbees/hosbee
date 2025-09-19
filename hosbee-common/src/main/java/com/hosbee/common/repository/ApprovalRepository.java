package com.hosbee.common.repository;

import com.hosbee.common.entity.Approval;
import com.hosbee.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long>, JpaSpecificationExecutor<Approval> {
    
    Optional<Approval> findByApprovalCode(String approvalCode);
    
    Page<Approval> findByApprover(User approver, Pageable pageable);
    
    Page<Approval> findByStatus(Approval.Status status, Pageable pageable);
    
    Page<Approval> findByTargetType(Approval.TargetType targetType, Pageable pageable);
    
    @Query("SELECT a FROM Approval a WHERE a.targetType = :targetType AND a.targetId = :targetId")
    List<Approval> findByTargetTypeAndTargetId(@Param("targetType") Approval.TargetType targetType, @Param("targetId") Long targetId);
    
    @Query("SELECT a FROM Approval a WHERE a.targetType = :targetType AND a.targetId = :targetId AND a.status = :status")
    List<Approval> findByTargetTypeAndTargetIdAndStatus(@Param("targetType") Approval.TargetType targetType, @Param("targetId") Long targetId, @Param("status") Approval.Status status);
    
    @Query("SELECT a FROM Approval a WHERE a.approver = :approver AND a.status = :status")
    Page<Approval> findByApproverAndStatus(@Param("approver") User approver, @Param("status") Approval.Status status, Pageable pageable);
    
    @Query("SELECT a FROM Approval a WHERE a.targetType = :targetType AND a.targetId = :targetId AND a.workflowStep = :step")
    Optional<Approval> findByTargetAndWorkflowStep(@Param("targetType") Approval.TargetType targetType, @Param("targetId") Long targetId, @Param("step") Integer step);
    
    long countByApprover(User approver);
    
    long countByStatus(Approval.Status status);
    
    long countByTargetType(Approval.TargetType targetType);
    
    long countByApproverAndStatus(User approver, Approval.Status status);
}