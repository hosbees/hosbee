package com.hosbee.admin.controller;

import com.hosbee.common.dto.*;
import com.hosbee.common.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ProjectService projectService;
    private final ProposalService proposalService;
    private final ApprovalService approvalService;
    private final BoardService boardService;

    // Dashboard Statistics
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = Map.of(
            "users", userService.getUserStatistics(),
            "projects", projectService.getProjectStatistics(),
            "proposals", proposalService.getProposalStatistics(),
            "approvals", approvalService.getApprovalStatistics()
        );
        return ResponseEntity.ok(stats);
    }

    // User Management
    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        Page<UserDTO> users = userService.searchUsers(role, status, keyword, pageable);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users/{userId}/activate")
    public ResponseEntity<UserDTO> activateUser(@PathVariable Long userId) {
        UserDTO user = userService.activateUser(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users/{userId}/deactivate")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable Long userId) {
        UserDTO user = userService.deactivateUser(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users/{userId}/change-role")
    public ResponseEntity<UserDTO> changeUserRole(
            @PathVariable Long userId, 
            @RequestParam String role) {
        UserDTO user = userService.changeUserRole(userId, role);
        return ResponseEntity.ok(user);
    }

    // Project Management
    @GetMapping("/projects")
    public ResponseEntity<Page<ProjectDTO>> getProjects(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        Page<ProjectDTO> projects = projectService.searchProjects(status, category, keyword, pageable);
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/projects/{projectId}/approve")
    public ResponseEntity<ProjectDTO> approveProject(@PathVariable Long projectId) {
        ProjectDTO project = projectService.publishProjectDTO(projectId);
        return ResponseEntity.ok(project);
    }

    @PostMapping("/projects/{projectId}/reject")
    public ResponseEntity<ProjectDTO> rejectProject(
            @PathVariable Long projectId,
            @RequestParam String reason) {
        ProjectDTO project = projectService.closeProjectDTO(projectId);
        return ResponseEntity.ok(project);
    }

    // Proposal Management  
    @GetMapping("/proposals")
    public ResponseEntity<Page<ProposalDTO>> getProposals(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        Page<ProposalDTO> proposals = proposalService.searchProposals(projectId, status, pageable);
        return ResponseEntity.ok(proposals);
    }

    // Board Management
    @GetMapping("/boards")
    public ResponseEntity<Page<BoardDTO>> getBoards(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isNotice,
            Pageable pageable) {
        Page<BoardDTO> boards = boardService.searchBoards(category, keyword, null, null, isNotice, pageable);
        return ResponseEntity.ok(boards);
    }

    @PostMapping("/boards/{boardId}/pin")
    public ResponseEntity<BoardDTO> pinBoard(@PathVariable Long boardId) {
        BoardDTO board = boardService.pinBoard(boardId);
        return ResponseEntity.ok(board);
    }

    @DeleteMapping("/boards/{boardId}/pin")
    public ResponseEntity<BoardDTO> unpinBoard(@PathVariable Long boardId) {
        BoardDTO board = boardService.unpinBoard(boardId);
        return ResponseEntity.ok(board);
    }

    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }

    // System Settings
    @GetMapping("/settings")
    public ResponseEntity<Map<String, Object>> getSystemSettings() {
        // TODO: Implement system settings retrieval
        Map<String, Object> settings = Map.of(
            "maintenanceMode", false,
            "maxFileUploadSize", "10MB",
            "allowUserRegistration", true
        );
        return ResponseEntity.ok(settings);
    }

    @PostMapping("/settings")
    public ResponseEntity<Map<String, Object>> updateSystemSettings(
            @RequestBody Map<String, Object> settings) {
        // TODO: Implement system settings update
        return ResponseEntity.ok(settings);
    }

    // System Health
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        // TODO: Implement system health check
        Map<String, Object> health = Map.of(
            "status", "UP",
            "database", "UP",
            "disk", Map.of("status", "UP", "free", "50GB"),
            "memory", Map.of("status", "UP", "used", "2GB", "free", "6GB")
        );
        return ResponseEntity.ok(health);
    }

    // Audit Logs
    @GetMapping("/audit-logs")
    public ResponseEntity<Page<Object>> getAuditLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable) {
        // TODO: Implement audit log retrieval
        // For now return empty page
        Page<Object> logs = Page.empty(pageable);
        return ResponseEntity.ok(logs);
    }
}