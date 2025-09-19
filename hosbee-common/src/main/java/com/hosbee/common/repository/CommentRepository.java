package com.hosbee.common.repository;

import com.hosbee.common.entity.Board;
import com.hosbee.common.entity.Comment;
import com.hosbee.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    
    Page<Comment> findByBoard(Board board, Pageable pageable);
    
    Page<Comment> findByAuthor(User author, Pageable pageable);
    
    Page<Comment> findByStatus(Comment.Status status, Pageable pageable);
    
    @Query("SELECT c FROM Comment c WHERE c.board = :board AND c.parent IS NULL ORDER BY c.createdAt ASC")
    List<Comment> findTopLevelCommentsByBoard(@Param("board") Board board);
    
    @Query("SELECT c FROM Comment c WHERE c.parent = :parent ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParent(@Param("parent") Comment parent);
    
    @Query("SELECT c FROM Comment c WHERE c.board = :board AND c.status = :status")
    Page<Comment> findByBoardAndStatus(@Param("board") Board board, @Param("status") Comment.Status status, Pageable pageable);
    
    @Query("SELECT c FROM Comment c WHERE c.board = :board AND c.parent IS NULL AND c.status = 'ACTIVE' ORDER BY c.createdAt ASC")
    Page<Comment> findActiveTopLevelCommentsByBoard(@Param("board") Board board, Pageable pageable);
    
    long countByBoard(Board board);
    
    long countByAuthor(User author);
    
    long countByStatus(Comment.Status status);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.board = :board AND c.status = 'ACTIVE'")
    long countActiveCommentsByBoard(@Param("board") Board board);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.createdAt >= :startDate AND c.createdAt < :endDate")
    long countByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT c FROM Comment c WHERE c.board = :board AND c.status = :status ORDER BY c.createdAt ASC")
    List<Comment> findByBoardAndStatusOrderByCreatedAtAsc(@Param("board") Board board, @Param("status") Comment.Status status);
    
    @Query("SELECT c FROM Comment c WHERE c.author = :author AND c.status = :status")
    Page<Comment> findByAuthorAndStatus(@Param("author") User author, @Param("status") Comment.Status status, Pageable pageable);
    
    @Query("SELECT c FROM Comment c WHERE c.parent = :parent AND c.status = :status")
    List<Comment> findByParentAndStatus(@Param("parent") Comment parent, @Param("status") Comment.Status status);
}