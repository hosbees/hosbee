package com.hosbee.common.dto;

import com.hosbee.common.entity.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    
    private Long id;
    
    @NotNull(message = "Board ID is required")
    private Long boardId;
    
    @NotNull(message = "Author ID is required")
    private Long authorId;
    private String authorName;
    
    private Long parentCommentId; // For nested comments
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private Comment.Status status;
    private Boolean isSecret;
    
    private Integer likeCount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private List<CommentDTO> replies; // Child comments
    
    public static CommentDTO fromEntity(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .boardId(comment.getBoard() != null ? comment.getBoard().getId() : null)
                .authorId(comment.getAuthor() != null ? comment.getAuthor().getId() : null)
                .authorName(comment.getAuthor() != null ? comment.getAuthor().getUsername() : null)
                .parentCommentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .content(comment.getContent())
                .status(comment.getStatus())
                .isSecret(comment.getIsSecret())
                .likeCount(comment.getLikeCount())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
    
    public Comment toEntity() {
        return Comment.builder()
                .id(this.id)
                .content(this.content)
                .status(this.status)
                .isSecret(this.isSecret != null ? this.isSecret : false)
                .likeCount(this.likeCount != null ? this.likeCount : 0)
                .build();
    }
}