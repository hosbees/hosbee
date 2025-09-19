package com.hosbee.common.service;

import com.hosbee.common.dto.BoardDTO;
import com.hosbee.common.entity.Board;
import com.hosbee.common.entity.User;
import com.hosbee.common.repository.BoardRepository;
import com.hosbee.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    
    public Page<BoardDTO> searchBoards(String category, String keyword, String tag, 
                                      Boolean isPinned, Boolean isNotice, Pageable pageable) {
        Specification<Board> spec = Specification.where(null);
        
        // Filter by status (only published boards)
        spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), Board.Status.ACTIVE));
        
        if (category != null) {
            try {
                Board.Category categoryEnum = Board.Category.valueOf(category.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), categoryEnum));
            } catch (IllegalArgumentException e) {
                // Invalid category, ignore
            }
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("content")), "%" + keyword.toLowerCase() + "%")
                ));
        }
        
        if (tag != null && !tag.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("tags")), "%" + tag.toLowerCase() + "%"));
        }
        
        if (isPinned != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isPinned"), isPinned));
        }
        
        if (isNotice != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isNotice"), isNotice));
        }
        
        return boardRepository.findAll(spec, pageable).map(BoardDTO::fromEntity);
    }
    
    public BoardDTO getBoardById(Long id) {
        Board board = boardRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + id));
        
        // Increase view count
        board.setViewCount(board.getViewCount() + 1);
        boardRepository.save(board);
        
        return BoardDTO.fromEntity(board);
    }
    
    public List<BoardDTO> getNotices() {
        List<Board> notices = boardRepository.findByIsFeaturedAndStatusOrderByCreatedAtDesc(
                true, Board.Status.ACTIVE);
        return notices.stream().map(BoardDTO::fromEntity).toList();
    }
    
    public List<BoardDTO> getPinnedBoards() {
        List<Board> pinnedBoards = boardRepository.findByIsPinnedAndStatusOrderByCreatedAtDesc(
                true, Board.Status.ACTIVE);
        return pinnedBoards.stream().map(BoardDTO::fromEntity).toList();
    }
    
    @Transactional
    public BoardDTO createBoard(BoardDTO boardDTO) {
        // TODO: Get current user from security context
        User currentUser = userRepository.findById(1L).orElse(null); // Temporary
        
        Board board = boardDTO.toEntity();
        board.setAuthor(currentUser);
        board.setStatus(Board.Status.ACTIVE); // Start as active
        board.setViewCount(0);
        board.setLikeCount(0);
        board.setCommentCount(0);
        
        Board savedBoard = boardRepository.save(board);
        log.info("Board created: {} by {}", savedBoard.getId(), currentUser.getUsername());
        
        return BoardDTO.fromEntity(savedBoard);
    }
    
    @Transactional
    public BoardDTO updateBoard(Long id, BoardDTO boardDTO) {
        Board board = boardRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + id));
        
        // TODO: Check if current user is the author or admin
        
        // Update fields
        if (boardDTO.getTitle() != null) {
            board.setTitle(boardDTO.getTitle());
        }
        if (boardDTO.getContent() != null) {
            board.setContent(boardDTO.getContent());
        }
        if (boardDTO.getCategory() != null) {
            board.setCategory(boardDTO.getCategory());
        }
        if (boardDTO.getTags() != null) {
            board.setTags(boardDTO.getTags());
        }
        if (boardDTO.getAttachmentFiles() != null) {
            board.setAttachmentFiles(boardDTO.getAttachmentFiles());
        }
        
        Board savedBoard = boardRepository.save(board);
        return BoardDTO.fromEntity(savedBoard);
    }
    
    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + id));
        
        // TODO: Check if current user is the author or admin
        
        // Soft delete by changing status
        board.setStatus(Board.Status.DELETED);
        boardRepository.save(board);
        log.info("Board deleted: {}", id);
    }
    
    @Transactional
    public BoardDTO likeBoard(Long id) {
        Board board = boardRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + id));
        
        // TODO: Check if user already liked this board (prevent duplicate likes)
        
        board.setLikeCount(board.getLikeCount() + 1);
        Board savedBoard = boardRepository.save(board);
        
        return BoardDTO.fromEntity(savedBoard);
    }
    
    @Transactional
    public BoardDTO pinBoard(Long id) {
        Board board = boardRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + id));
        
        // TODO: Check if current user is admin
        
        board.setIsPinned(true);
        // Board pinned timestamp handled by isPinned field
        Board savedBoard = boardRepository.save(board);
        log.info("Board pinned: {}", id);
        
        return BoardDTO.fromEntity(savedBoard);
    }
    
    @Transactional
    public BoardDTO unpinBoard(Long id) {
        Board board = boardRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + id));
        
        // TODO: Check if current user is admin
        
        board.setIsPinned(false);
        // Pinned timestamp handled by isPinned field
        Board savedBoard = boardRepository.save(board);
        log.info("Board unpinned: {}", id);
        
        return BoardDTO.fromEntity(savedBoard);
    }
    
    @Transactional
    public BoardDTO publishBoard(Long id) {
        Board board = boardRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + id));
        
        // TODO: Check if current user is the author or admin
        
        board.setStatus(Board.Status.ACTIVE);
        Board savedBoard = boardRepository.save(board);
        log.info("Board published: {}", id);
        
        return BoardDTO.fromEntity(savedBoard);
    }
    
    @Transactional
    public void increaseCommentCount(Long boardId) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));
        
        board.setCommentCount(board.getCommentCount() + 1);
        boardRepository.save(board);
    }
    
    @Transactional
    public void decreaseCommentCount(Long boardId) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));
        
        if (board.getCommentCount() > 0) {
            board.setCommentCount(board.getCommentCount() - 1);
            boardRepository.save(board);
        }
    }
}