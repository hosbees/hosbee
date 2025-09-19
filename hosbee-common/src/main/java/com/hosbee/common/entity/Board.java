package com.hosbee.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "boards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    @Builder.Default
    private Boolean isPinned = false;
    
    @Builder.Default
    private Boolean isFeatured = false;
    
    @Builder.Default
    private Integer viewCount = 0;
    
    @Builder.Default
    private Integer likeCount = 0;
    
    @Builder.Default
    private Integer commentCount = 0;
    
    @Column(columnDefinition = "JSON")
    private String attachmentFiles;
    
    @Column(columnDefinition = "JSON")
    private String tags;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.ACTIVE;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Comment> comments;
    
    public enum Category {
        NOTICE, FREE, QNA, FAQ, EVENT
    }
    
    public enum Status {
        ACTIVE, HIDDEN, DELETED
    }
}