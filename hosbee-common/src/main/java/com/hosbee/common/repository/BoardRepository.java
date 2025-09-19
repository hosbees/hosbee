package com.hosbee.common.repository;

import com.hosbee.common.entity.Board;
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
public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {
    
    Page<Board> findByCategory(Board.Category category, Pageable pageable);
    
    Page<Board> findByAuthor(User author, Pageable pageable);
    
    Page<Board> findByStatus(Board.Status status, Pageable pageable);
    
    @Query("SELECT b FROM Board b WHERE b.category = :category AND b.status = :status")
    Page<Board> findByCategoryAndStatus(@Param("category") Board.Category category, @Param("status") Board.Status status, Pageable pageable);
    
    @Query("SELECT b FROM Board b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    Page<Board> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT b FROM Board b WHERE b.category = :category AND (b.title LIKE %:keyword% OR b.content LIKE %:keyword%)")
    Page<Board> findByCategoryAndKeyword(@Param("category") Board.Category category, @Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT b FROM Board b WHERE b.isPinned = true AND b.status = 'ACTIVE' ORDER BY b.createdAt DESC")
    List<Board> findPinnedBoards();
    
    @Query("SELECT b FROM Board b WHERE b.isFeatured = true AND b.status = 'ACTIVE' ORDER BY b.viewCount DESC")
    List<Board> findFeaturedBoards();
    
    @Query("SELECT b FROM Board b WHERE b.category = :category AND b.status = 'ACTIVE' ORDER BY b.viewCount DESC")
    List<Board> findPopularBoardsByCategory(@Param("category") Board.Category category);
    
    long countByCategory(Board.Category category);
    
    long countByAuthor(User author);
    
    long countByStatus(Board.Status status);
    
    @Query("SELECT COUNT(b) FROM Board b WHERE b.createdAt >= :startDate AND b.createdAt < :endDate")
    long countByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT b FROM Board b WHERE b.isFeatured = :isFeatured AND b.status = :status ORDER BY b.createdAt DESC")
    List<Board> findByIsFeaturedAndStatusOrderByCreatedAtDesc(@Param("isFeatured") boolean isFeatured, @Param("status") Board.Status status);
    
    @Query("SELECT b FROM Board b WHERE b.isPinned = :isPinned AND b.status = :status ORDER BY b.createdAt DESC")
    List<Board> findByIsPinnedAndStatusOrderByCreatedAtDesc(@Param("isPinned") boolean isPinned, @Param("status") Board.Status status);
}