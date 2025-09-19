package com.hosbee.common.service;

import com.hosbee.common.dto.ApprovalDTO;
import com.hosbee.common.entity.Approval;
import com.hosbee.common.entity.User;
import com.hosbee.common.repository.ApprovalRepository;
import com.hosbee.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovalService {
    
    private final ApprovalRepository approvalRepository;
    private final UserRepository userRepository;
    
    public Page<ApprovalDTO> searchApprovals(String status, String requestType, String priority, Pageable pageable) {
        Specification<Approval> spec = Specification.where(null);
        
        if (status != null) {
            try {
                Approval.Status statusEnum = Approval.Status.valueOf(status.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), statusEnum));
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }
        
        if (requestType != null) {
            try {
                Approval.TargetType typeEnum = Approval.TargetType.valueOf(requestType.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("targetType"), typeEnum));
            } catch (IllegalArgumentException e) {
                // Invalid request type, ignore
            }
        }
        
        return approvalRepository.findAll(spec, pageable).map(ApprovalDTO::fromEntity);
    }
    
    public ApprovalDTO getApprovalById(Long id) {
        Approval approval = approvalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Approval not found with id: " + id));
        
        return ApprovalDTO.fromEntity(approval);
    }
    
    public Page<ApprovalDTO> getPendingApprovals(Pageable pageable) {
        return approvalRepository.findByStatus(Approval.Status.PENDING, pageable)
                .map(ApprovalDTO::fromEntity);
    }
    
    public Page<ApprovalDTO> getMyApprovalRequests(Pageable pageable) {
        // TODO: Get current user from security context
        User currentUser = userRepository.findById(1L).orElse(null); // Temporary
        
        return approvalRepository.findByApprover(currentUser, pageable)
                .map(ApprovalDTO::fromEntity);
    }
    
    @Transactional
    public ApprovalDTO createApprovalRequest(ApprovalDTO approvalDTO) {
        // TODO: Get current user from security context
        User currentUser = userRepository.findById(1L).orElse(null); // Temporary
        
        Approval approval = approvalDTO.toEntity();
        approval.setStatus(Approval.Status.PENDING);
        approval.setRequestedAt(LocalDateTime.now());
        
        // Generate approval code
        approval.setApprovalCode("APV" + System.currentTimeMillis());
        
        // Auto-assign approver based on target type
        User approver = findApproverForTargetType(approval.getTargetType());
        if (approver != null) {
            approval.setApprover(approver);
        }
        
        Approval savedApproval = approvalRepository.save(approval);
        log.info("Approval request created: {} for {} by {}", 
                savedApproval.getId(), approval.getTargetType(), currentUser.getUsername());
        
        return ApprovalDTO.fromEntity(savedApproval);
    }
    
    @Transactional
    public ApprovalDTO approveRequest(Long id, String comments) {
        Approval approval = approvalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Approval not found with id: " + id));
        
        if (!Approval.Status.PENDING.equals(approval.getStatus())) {
            throw new IllegalStateException("Only pending approvals can be approved");
        }
        
        // TODO: Get current user from security context
        User currentUser = userRepository.findById(1L).orElse(null); // Temporary
        
        approval.setStatus(Approval.Status.APPROVED);
        approval.setApprover(currentUser);
        approval.setProcessedAt(LocalDateTime.now());
        approval.setComment(comments);
        
        Approval savedApproval = approvalRepository.save(approval);
        log.info("Approval {} approved by {}", id, currentUser.getUsername());
        
        // Process the approved request
        processApprovedRequest(approval);
        
        return ApprovalDTO.fromEntity(savedApproval);
    }
    
    @Transactional
    public ApprovalDTO rejectRequest(Long id, String reason) {
        Approval approval = approvalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Approval not found with id: " + id));
        
        if (!Approval.Status.PENDING.equals(approval.getStatus())) {
            throw new IllegalStateException("Only pending approvals can be rejected");
        }
        
        // TODO: Get current user from security context
        User currentUser = userRepository.findById(1L).orElse(null); // Temporary
        
        approval.setStatus(Approval.Status.REJECTED);
        approval.setApprover(currentUser);
        approval.setProcessedAt(LocalDateTime.now());
        approval.setComment(reason);
        
        Approval savedApproval = approvalRepository.save(approval);
        log.info("Approval {} rejected by {}: {}", id, currentUser.getUsername(), reason);
        
        return ApprovalDTO.fromEntity(savedApproval);
    }
    
    @Transactional
    public ApprovalDTO assignApprover(Long id, Long approverId) {
        Approval approval = approvalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Approval not found with id: " + id));
        
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + approverId));
        
        if (!Approval.Status.PENDING.equals(approval.getStatus())) {
            throw new IllegalStateException("Only pending approvals can be reassigned");
        }
        
        approval.setApprover(approver);
        Approval savedApproval = approvalRepository.save(approval);
        log.info("Approval {} assigned to {}", id, approver.getUsername());
        
        return ApprovalDTO.fromEntity(savedApproval);
    }
    
    @Transactional
    public ApprovalDTO escalateRequest(Long id) {
        Approval approval = approvalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Approval not found with id: " + id));
        
        if (!Approval.Status.PENDING.equals(approval.getStatus())) {
            throw new IllegalStateException("Only pending approvals can be escalated");
        }
        
        // Escalate to higher authority
        User higherApprover = findHigherApprover(approval.getApprover());
        if (higherApprover != null) {
            approval.setApprover(higherApprover);
            // Note: No priority field in current Approval entity
        }
        
        Approval savedApproval = approvalRepository.save(approval);
        log.info("Approval {} escalated to {}", id, 
                higherApprover != null ? higherApprover.getUsername() : "higher authority");
        
        return ApprovalDTO.fromEntity(savedApproval);
    }
    
    public Object getApprovalStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Count by status
        Map<String, Long> statusCounts = new HashMap<>();
        statusCounts.put("pending", approvalRepository.countByStatus(Approval.Status.PENDING));
        statusCounts.put("approved", approvalRepository.countByStatus(Approval.Status.APPROVED));
        statusCounts.put("rejected", approvalRepository.countByStatus(Approval.Status.REJECTED));
        
        // Count by target type
        Map<String, Long> typeCounts = new HashMap<>();
        for (Approval.TargetType type : Approval.TargetType.values()) {
            typeCounts.put(type.name().toLowerCase(), approvalRepository.countByTargetType(type));
        }
        
        stats.put("statusCounts", statusCounts);
        stats.put("typeCounts", typeCounts);
        stats.put("totalRequests", approvalRepository.count());
        
        return stats;
    }
    
    private User findApproverForTargetType(Approval.TargetType targetType) {
        // TODO: Implement logic to find appropriate approver based on target type
        // For now, return a default admin user
        return userRepository.findByRole(User.Role.ADMIN, org.springframework.data.domain.Pageable.unpaged())
                .stream().findFirst().orElse(null);
    }
    
    private User findHigherApprover(User currentApprover) {
        // TODO: Implement organizational hierarchy logic
        // For now, find any other admin
        return userRepository.findByRole(User.Role.ADMIN, org.springframework.data.domain.Pageable.unpaged()).stream()
                .filter(user -> !user.equals(currentApprover))
                .findFirst()
                .orElse(null);
    }
    
    private void processApprovedRequest(Approval approval) {
        // TODO: Implement specific processing logic based on target type
        switch (approval.getTargetType()) {
            case PROJECT -> processProjectApproval(approval);
            case PROPOSAL -> processProposalApproval(approval);
            case CONTRACT -> processContractApproval(approval);
            case USER_REGISTRATION -> processUserApproval(approval);
        }
    }
    
    private void processProjectApproval(Approval approval) {
        // TODO: Update project status to approved
        log.info("Processing project approval for target ID: {}", approval.getTargetId());
    }
    
    private void processProposalApproval(Approval approval) {
        // TODO: Update proposal status to approved
        log.info("Processing proposal approval for target ID: {}", approval.getTargetId());
    }
    
    private void processContractApproval(Approval approval) {
        // TODO: Update contract status to approved
        log.info("Processing contract approval for target ID: {}", approval.getTargetId());
    }
    
    private void processUserApproval(Approval approval) {
        // TODO: Activate user account
        log.info("Processing user approval for target ID: {}", approval.getTargetId());
    }
}