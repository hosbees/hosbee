package com.hosbee.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "negotiations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Negotiation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false)
    private Proposal proposal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;
    
    @Column(nullable = false)
    private Integer roundNumber;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;
    
    private Integer deliveryDays;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PENDING;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    public enum Status {
        PENDING, ACCEPTED, REJECTED, COUNTERED
    }
}