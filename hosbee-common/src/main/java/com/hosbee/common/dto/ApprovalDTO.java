package com.hosbee.common.dto;

import com.hosbee.common.entity.Approval;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalDTO {
    
    private Long id;
    
    @NotNull(message = "Target type is required")
    private Approval.TargetType targetType;
    
    @NotNull(message = "Target ID is required")
    private Long targetId;
    
    private Integer workflowStep;
    
    @NotNull(message = "Approver ID is required")
    private Long approverId;
    private String approverName;
    
    private Approval.ApproverRole approverRole;
    
    private Approval.Status status;
    
    @NotBlank(message = "Comment is required")
    private String comment;
    
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String approvalCode;
    private Approval.RejectionReason rejectionReason;
    private Long delegatedToId;
    
    public static ApprovalDTO fromEntity(Approval approval) {
        return ApprovalDTO.builder()
                .id(approval.getId())
                .targetType(approval.getTargetType())
                .targetId(approval.getTargetId())
                .workflowStep(approval.getWorkflowStep())
                .approverId(approval.getApprover() != null ? approval.getApprover().getId() : null)
                .approverName(approval.getApprover() != null ? approval.getApprover().getUsername() : null)
                .approverRole(approval.getApproverRole())
                .status(approval.getStatus())
                .comment(approval.getComment())
                .requestedAt(approval.getRequestedAt())
                .processedAt(approval.getProcessedAt())
                .approvalCode(approval.getApprovalCode())
                .rejectionReason(approval.getRejectionReason())
                .delegatedToId(approval.getDelegatedTo() != null ? approval.getDelegatedTo().getId() : null)
                .build();
    }
    
    public Approval toEntity() {
        return Approval.builder()
                .id(this.id)
                .targetType(this.targetType)
                .targetId(this.targetId)
                .workflowStep(this.workflowStep)
                .approverRole(this.approverRole)
                .status(this.status)
                .comment(this.comment)
                .requestedAt(this.requestedAt)
                .processedAt(this.processedAt)
                .approvalCode(this.approvalCode)
                .rejectionReason(this.rejectionReason)
                .build();
    }
}