package com.hosbee.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "approvals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Approval {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 20)
    private String approvalCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetType targetType;
    
    @Column(nullable = false)
    private Long targetId;
    
    @Column(nullable = false)
    private Integer workflowStep;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApproverRole approverRole;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PENDING;
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @Enumerated(EnumType.STRING)
    private RejectionReason rejectionReason;
    
    @CreationTimestamp
    private LocalDateTime requestedAt;
    
    private LocalDateTime processedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegated_to")
    private User delegatedTo;
    
    public enum TargetType {
        PROJECT, PROPOSAL, USER_REGISTRATION, CONTRACT
    }
    
    public enum ApproverRole {
        MANAGER, ADMIN, SENIOR_ADMIN
    }
    
    public enum Status {
        PENDING, APPROVED, REJECTED, DELEGATED
    }
    
    public enum RejectionReason {
        BUDGET, REQUIREMENTS, TIMELINE, QUALITY, OTHER
    }
}