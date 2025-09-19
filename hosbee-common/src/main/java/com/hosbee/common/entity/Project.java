package com.hosbee.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 20)
    private String projectCode;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String requirements;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.DRAFT;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal budgetMin;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal budgetMax;
    
    @Column(length = 3)
    @Builder.Default
    private String currency = "KRW";
    
    @Column(nullable = false)
    private LocalDate deadline;
    
    private LocalDateTime biddingDeadline;
    
    @Column(columnDefinition = "JSON")
    private String requiredSkills;
    
    @Column(columnDefinition = "JSON")
    private String attachmentFiles;
    
    @Builder.Default
    private Integer viewCount = 0;
    
    @Builder.Default
    private Integer proposalCount = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Proposal> proposals;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Contract> contracts;
    
    public enum Category {
        WEB, MOBILE, SYSTEM, DESIGN, CONSULTING
    }
    
    public enum Status {
        DRAFT, PUBLISHED, IN_BIDDING, AWARDED, IN_PROGRESS, COMPLETED, CANCELLED
    }
    
    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }
}