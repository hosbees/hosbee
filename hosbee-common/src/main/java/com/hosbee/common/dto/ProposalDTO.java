package com.hosbee.common.dto;

import com.hosbee.common.entity.Proposal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalDTO {
    
    private Long id;
    
    @NotNull(message = "Project ID is required")
    private Long projectId;
    private String projectTitle;
    private String projectCode;
    
    @NotNull(message = "Freelancer ID is required")
    private Long freelancerId;
    private String freelancerName;
    
    @Positive(message = "Proposed amount must be positive")
    private BigDecimal proposedAmount;
    
    @Positive(message = "Estimated duration must be positive")
    private Integer estimatedDuration;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private String portfolioLinks;
    private String attachmentFiles;
    
    private Proposal.Status status;
    private LocalDateTime submittedAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static ProposalDTO fromEntity(Proposal proposal) {
        return ProposalDTO.builder()
                .id(proposal.getId())
                .projectId(proposal.getProject() != null ? proposal.getProject().getId() : null)
                .projectTitle(proposal.getProject() != null ? proposal.getProject().getTitle() : null)
                .projectCode(proposal.getProject() != null ? proposal.getProject().getProjectCode() : null)
                .freelancerId(proposal.getProposer() != null ? proposal.getProposer().getId() : null)
                .freelancerName(proposal.getProposer() != null ? proposal.getProposer().getUsername() : null)
                .proposedAmount(proposal.getPrice())
                .estimatedDuration(proposal.getDeliveryDays())
                .content(proposal.getContent())
                .portfolioLinks(proposal.getAttachmentFiles())
                .attachmentFiles(proposal.getAttachmentFiles())
                .status(proposal.getStatus())
                .submittedAt(proposal.getSubmittedAt())
                .createdAt(proposal.getCreatedAt())
                .updatedAt(proposal.getUpdatedAt())
                .build();
    }
    
    public Proposal toEntity() {
        return Proposal.builder()
                .id(this.id)
                .price(this.proposedAmount)
                .deliveryDays(this.estimatedDuration)
                .content(this.content)
                .attachmentFiles(this.attachmentFiles)
                .status(this.status)
                .submittedAt(this.submittedAt)
                .build();
    }
}