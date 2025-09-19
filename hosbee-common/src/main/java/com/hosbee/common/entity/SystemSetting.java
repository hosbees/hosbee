package com.hosbee.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemSetting {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String category;
    
    @Column(nullable = false, length = 100)
    private String settingKey;
    
    @Column(columnDefinition = "TEXT")
    private String settingValue;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Builder.Default
    private Boolean isPublic = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}