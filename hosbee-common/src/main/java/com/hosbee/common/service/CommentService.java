package com.hosbee.common.service;

import com.hosbee.common.dto.CommentDTO;
import com.hosbee.common.entity.Board;
import com.hosbee.common.entity.Comment;
import com.hosbee.common.entity.User;
import com.hosbee.common.repository.BoardRepository;
import com.hosbee.common.repository.CommentRepository;
import com.hosbee.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardService boardService;
    
    public List<CommentDTO> getCommentsByBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));
        
        List<Comment> comments = commentRepository.findByBoardAndStatusOrderByCreatedAtAsc(
                board, Comment.Status.ACTIVE);
        
        return buildCommentTree(comments);
    }
    
    public Page<CommentDTO> getCommentsByAuthor(Long authorId, Pageable pageable) {
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + authorId));
        
        return commentRepository.findByAuthorAndStatus(author, Comment.Status.ACTIVE, pageable)
                .map(CommentDTO::fromEntity);
    }
    
    @Transactional
    public CommentDTO createComment(CommentDTO commentDTO) {
        // TODO: Get current user from security context
        User currentUser = userRepository.findById(1L).orElse(null); // Temporary
        
        Board board = boardRepository.findById(commentDTO.getBoardId())
            .orElseThrow(() -> new IllegalArgumentException("Board not found"));
        
        Comment comment = commentDTO.toEntity();
        comment.setBoard(board);
        comment.setAuthor(currentUser);
        comment.setStatus(Comment.Status.ACTIVE);
        comment.setLikeCount(0);
        
        // Handle parent comment for nested comments
        if (commentDTO.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findById(commentDTO.getParentCommentId())
                .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
            
            comment.setParent(parentComment);
            // Depth handling removed as Comment entity doesn't have depth field
        }
        
        Comment savedComment = commentRepository.save(comment);
        
        // Increase board comment count
        boardService.increaseCommentCount(board.getId());
        
        log.info("Comment created: {} on board {} by {}", 
                savedComment.getId(), board.getId(), currentUser.getUsername());
        
        return CommentDTO.fromEntity(savedComment);
    }
    
    @Transactional
    public CommentDTO updateComment(Long id, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + id));
        
        // TODO: Check if current user is the author or admin
        
        if (commentDTO.getContent() != null) {
            comment.setContent(commentDTO.getContent());
        }
        // Attachment files not supported in Comment entity
        
        Comment savedComment = commentRepository.save(comment);
        return CommentDTO.fromEntity(savedComment);
    }
    
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + id));
        
        // TODO: Check if current user is the author or admin
        
        // Check if comment has replies
        List<Comment> replies = commentRepository.findByParentAndStatus(comment, Comment.Status.ACTIVE);
        
        if (!replies.isEmpty()) {
            // Soft delete - change content to indicate deletion but keep structure for replies
            comment.setContent("[삭제된 댓글입니다]");
            comment.setStatus(Comment.Status.DELETED);
            commentRepository.save(comment);
        } else {
            // Hard delete if no replies
            comment.setStatus(Comment.Status.DELETED);
            commentRepository.save(comment);
        }
        
        // Decrease board comment count
        boardService.decreaseCommentCount(comment.getBoard().getId());
        
        log.info("Comment deleted: {}", id);
    }
    
    @Transactional
    public CommentDTO likeComment(Long id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + id));
        
        // TODO: Check if user already liked this comment (prevent duplicate likes)
        
        comment.setLikeCount(comment.getLikeCount() + 1);
        Comment savedComment = commentRepository.save(comment);
        
        return CommentDTO.fromEntity(savedComment);
    }
    
    @Transactional
    public void reportComment(Long id, String reason) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + id));
        
        // TODO: Implement comment reporting system
        log.info("Comment {} reported: {}", id, reason);
    }
    
    private List<CommentDTO> buildCommentTree(List<Comment> comments) {
        // Build a hierarchical structure of comments
        return comments.stream()
                .filter(comment -> comment.getParent() == null) // Top-level comments only
                .map(this::buildCommentWithReplies)
                .toList();
    }
    
    private CommentDTO buildCommentWithReplies(Comment comment) {
        CommentDTO commentDTO = CommentDTO.fromEntity(comment);
        
        // Get replies for this comment
        List<Comment> replies = commentRepository.findByParentAndStatus(
                comment, Comment.Status.ACTIVE);
        
        if (!replies.isEmpty()) {
            List<CommentDTO> replyDTOs = replies.stream()
                    .map(this::buildCommentWithReplies) // Recursive for nested replies
                    .toList();
            commentDTO.setReplies(replyDTOs);
        }
        
        return commentDTO;
    }
}