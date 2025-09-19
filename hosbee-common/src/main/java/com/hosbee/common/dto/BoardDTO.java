package com.hosbee.common.dto;

import com.hosbee.common.entity.Board;
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
public class BoardDTO {
    
    private Long id;
    
    @NotNull(message = "Category is required")
    private Board.Category category;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "Author ID is required")
    private Long authorId;
    private String authorName;
    
    private String tags;
    private String attachmentFiles;
    
    private Board.Status status;
    private Boolean isPinned;
    private Boolean isFeatured;
    
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static BoardDTO fromEntity(Board board) {
        return BoardDTO.builder()
                .id(board.getId())
                .category(board.getCategory())
                .title(board.getTitle())
                .content(board.getContent())
                .authorId(board.getAuthor() != null ? board.getAuthor().getId() : null)
                .authorName(board.getAuthor() != null ? board.getAuthor().getUsername() : null)
                .tags(board.getTags())
                .attachmentFiles(board.getAttachmentFiles())
                .status(board.getStatus())
                .isPinned(board.getIsPinned())
                .isFeatured(board.getIsFeatured())
                .viewCount(board.getViewCount())
                .likeCount(board.getLikeCount())
                .commentCount(board.getCommentCount())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
    
    public Board toEntity() {
        return Board.builder()
                .id(this.id)
                .category(this.category)
                .title(this.title)
                .content(this.content)
                .tags(this.tags)
                .attachmentFiles(this.attachmentFiles)
                .status(this.status)
                .isPinned(this.isPinned != null ? this.isPinned : false)
                .isFeatured(this.isFeatured != null ? this.isFeatured : false)
                .viewCount(this.viewCount != null ? this.viewCount : 0)
                .likeCount(this.likeCount != null ? this.likeCount : 0)
                .commentCount(this.commentCount != null ? this.commentCount : 0)
                .build();
    }
}