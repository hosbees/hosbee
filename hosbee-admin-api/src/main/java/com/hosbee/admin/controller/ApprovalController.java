package com.hosbee.admin.controller;

import com.hosbee.common.dto.ApprovalDTO;
import com.hosbee.common.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @GetMapping
    public ResponseEntity<Page<ApprovalDTO>> getApprovals(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String requestType,
            @RequestParam(required = false) String priority,
            Pageable pageable) {
        
        Page<ApprovalDTO> approvals = approvalService.searchApprovals(status, requestType, priority, pageable);
        return ResponseEntity.ok(approvals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApprovalDTO> getApproval(@PathVariable Long id) {
        ApprovalDTO approval = approvalService.getApprovalById(id);
        return ResponseEntity.ok(approval);
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<ApprovalDTO>> getPendingApprovals(Pageable pageable) {
        Page<ApprovalDTO> pendingApprovals = approvalService.getPendingApprovals(pageable);
        return ResponseEntity.ok(pendingApprovals);
    }

    @GetMapping("/my-requests")
    public ResponseEntity<Page<ApprovalDTO>> getMyRequests(Pageable pageable) {
        Page<ApprovalDTO> myRequests = approvalService.getMyApprovalRequests(pageable);
        return ResponseEntity.ok(myRequests);
    }

    @PostMapping
    public ResponseEntity<ApprovalDTO> createApprovalRequest(@Valid @RequestBody ApprovalDTO approvalDTO) {
        ApprovalDTO createdApproval = approvalService.createApprovalRequest(approvalDTO);
        return ResponseEntity.ok(createdApproval);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApprovalDTO> approveRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String comments) {
        ApprovalDTO approvedRequest = approvalService.approveRequest(id, comments);
        return ResponseEntity.ok(approvedRequest);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApprovalDTO> rejectRequest(
            @PathVariable Long id,
            @RequestParam String reason) {
        ApprovalDTO rejectedRequest = approvalService.rejectRequest(id, reason);
        return ResponseEntity.ok(rejectedRequest);
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<ApprovalDTO> assignApprover(
            @PathVariable Long id,
            @RequestParam Long approverId) {
        ApprovalDTO assignedApproval = approvalService.assignApprover(id, approverId);
        return ResponseEntity.ok(assignedApproval);
    }

    @PostMapping("/{id}/escalate")
    public ResponseEntity<ApprovalDTO> escalateRequest(@PathVariable Long id) {
        ApprovalDTO escalatedRequest = approvalService.escalateRequest(id);
        return ResponseEntity.ok(escalatedRequest);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getApprovalStats() {
        Object stats = approvalService.getApprovalStatistics();
        return ResponseEntity.ok(stats);
    }
}