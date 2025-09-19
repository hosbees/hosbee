package com.hosbee.common.dto;

import com.hosbee.common.entity.Project;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {
    
    private Long id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private String requirements;
    
    @NotNull(message = "Category is required")
    private Project.Category category;
    
    private Project.Priority priority;
    
    @Positive(message = "Budget min must be positive")
    private BigDecimal budgetMin;
    
    @Positive(message = "Budget max must be positive")
    private BigDecimal budgetMax;
    
    private LocalDate deadline;
    private LocalDateTime biddingDeadline;
    
    private String requiredSkills;
    private String attachmentFiles;
    
    private String projectCode;
    private Project.Status status;
    private Integer viewCount;
    private Integer proposalCount;
    
    private Long clientId;
    private String clientName;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static ProjectDTO fromEntity(Project project) {
        return ProjectDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .requirements(project.getRequirements())
                .category(project.getCategory())
                .priority(project.getPriority())
                .budgetMin(project.getBudgetMin())
                .budgetMax(project.getBudgetMax())
                .deadline(project.getDeadline())
                .biddingDeadline(project.getBiddingDeadline())
                .requiredSkills(project.getRequiredSkills())
                .attachmentFiles(project.getAttachmentFiles())
                .projectCode(project.getProjectCode())
                .status(project.getStatus())
                .viewCount(project.getViewCount())
                .proposalCount(project.getProposalCount())
                .clientId(project.getClient() != null ? project.getClient().getId() : null)
                .clientName(project.getClient() != null ? project.getClient().getUsername() : null)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
    
    public Project toEntity() {
        return Project.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .requirements(this.requirements)
                .category(this.category)
                .priority(this.priority)
                .budgetMin(this.budgetMin)
                .budgetMax(this.budgetMax)
                .deadline(this.deadline)
                .biddingDeadline(this.biddingDeadline)
                .requiredSkills(this.requiredSkills)
                .attachmentFiles(this.attachmentFiles)
                .projectCode(this.projectCode)
                .status(this.status)
                .viewCount(this.viewCount != null ? this.viewCount : 0)
                .proposalCount(this.proposalCount != null ? this.proposalCount : 0)
                .build();
    }
}