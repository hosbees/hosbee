package com.hosbee.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    private RelatedType relatedType;
    
    private Long relatedId;
    
    @Builder.Default
    private Boolean isRead = false;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    public enum Type {
        PROJECT_CREATED, PROPOSAL_RECEIVED, APPROVAL_REQUEST, 
        CONTRACT_SIGNED, PAYMENT_DUE, SYSTEM
    }
    
    public enum RelatedType {
        PROJECT, PROPOSAL, CONTRACT, USER, BOARD
    }
}