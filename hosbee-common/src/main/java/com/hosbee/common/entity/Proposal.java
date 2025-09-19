package com.hosbee.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "proposals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Proposal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 20)
    private String proposalCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposer_id", nullable = false)
    private User proposer;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(columnDefinition = "TEXT")
    private String approachMethodology;
    
    @Column(columnDefinition = "JSON")
    private String deliverables;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;
    
    @Column(length = 3)
    @Builder.Default
    private String currency = "KRW";
    
    @Column(nullable = false)
    private Integer deliveryDays;
    
    @Column(columnDefinition = "JSON")
    private String milestones;
    
    @Column(columnDefinition = "TEXT")
    private String paymentTerms;
    
    private Integer warrantyPeriod;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.DRAFT;
    
    @Column(columnDefinition = "JSON")
    private String attachmentFiles;
    
    private LocalDateTime submittedAt;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "proposal", cascade = CascadeType.ALL)
    private List<Negotiation> negotiations;
    
    @OneToMany(mappedBy = "proposal", cascade = CascadeType.ALL)
    private List<Contract> contracts;
    
    public enum Status {
        DRAFT, SUBMITTED, UNDER_REVIEW, NEGOTIATING, ACCEPTED, REJECTED, WITHDRAWN
    }
}