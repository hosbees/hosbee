package com.hosbee.common.service;

import com.hosbee.common.dto.ProposalDTO;
import com.hosbee.common.entity.Project;
import com.hosbee.common.entity.Proposal;
import com.hosbee.common.entity.User;
import com.hosbee.common.repository.ProjectRepository;
import com.hosbee.common.repository.ProposalRepository;
import com.hosbee.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProposalService {
    
    private final ProposalRepository proposalRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public Proposal createProposal(Proposal proposal, Long projectId, Long proposerId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));
        
        User proposer = userRepository.findById(proposerId)
            .orElseThrow(() -> new IllegalArgumentException("Proposer not found with id: " + proposerId));
        
        if (!Project.Status.IN_BIDDING.equals(project.getStatus())) {
            throw new IllegalStateException("Project is not accepting proposals");
        }
        
        if (proposalRepository.existsByProjectAndProposer(project, proposer)) {
            throw new IllegalStateException("Proposer has already submitted a proposal for this project");
        }
        
        proposal.setProject(project);
        proposal.setProposer(proposer);
        proposal.setProposalCode(generateProposalCode());
        proposal.setStatus(Proposal.Status.DRAFT);
        
        return proposalRepository.save(proposal);
    }
    
    public Optional<Proposal> findById(Long id) {
        return proposalRepository.findById(id);
    }
    
    public Optional<Proposal> findByProposalCode(String proposalCode) {
        return proposalRepository.findByProposalCode(proposalCode);
    }
    
    public Page<Proposal> findAll(Pageable pageable) {
        return proposalRepository.findAll(pageable);
    }
    
    public Page<Proposal> findByProject(Project project, Pageable pageable) {
        return proposalRepository.findByProject(project, pageable);
    }
    
    public Page<Proposal> findByProposer(User proposer, Pageable pageable) {
        return proposalRepository.findByProposer(proposer, pageable);
    }
    
    public Page<Proposal> findByStatus(Proposal.Status status, Pageable pageable) {
        return proposalRepository.findByStatus(status, pageable);
    }
    
    public List<Proposal> findByProjectAndStatus(Project project, Proposal.Status status) {
        return proposalRepository.findByProjectAndStatus(project, status);
    }
    
    public List<Proposal> findByProjectOrderByPrice(Project project) {
        return proposalRepository.findByProjectOrderByPriceAsc(project);
    }
    
    public List<Proposal> findByProjectAndPriceRange(Project project, BigDecimal minPrice, BigDecimal maxPrice) {
        return proposalRepository.findByProjectAndPriceRange(project, minPrice, maxPrice);
    }
    
    @Transactional
    public Proposal updateProposal(Long id, Proposal updateData) {
        Proposal proposal = proposalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Proposal not found with id: " + id));
        
        if (!Proposal.Status.DRAFT.equals(proposal.getStatus())) {
            throw new IllegalStateException("Only draft proposals can be updated");
        }
        
        updateProposalFields(proposal, updateData);
        return proposalRepository.save(proposal);
    }
    
    @Transactional
    public Proposal submitProposal(Long id) {
        Proposal proposal = proposalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Proposal not found with id: " + id));
        
        if (!Proposal.Status.DRAFT.equals(proposal.getStatus())) {
            throw new IllegalStateException("Only draft proposals can be submitted");
        }
        
        validateProposalForSubmission(proposal);
        
        proposal.setStatus(Proposal.Status.SUBMITTED);
        proposal.setSubmittedAt(LocalDateTime.now());
        
        log.info("Proposal {} submitted", proposal.getProposalCode());
        return proposalRepository.save(proposal);
    }
    
    @Transactional
    public Proposal reviewProposal(Long id) {
        Proposal proposal = proposalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Proposal not found with id: " + id));
        
        if (!Proposal.Status.SUBMITTED.equals(proposal.getStatus())) {
            throw new IllegalStateException("Only submitted proposals can be reviewed");
        }
        
        proposal.setStatus(Proposal.Status.UNDER_REVIEW);
        log.info("Proposal {} under review", proposal.getProposalCode());
        return proposalRepository.save(proposal);
    }
    
    @Transactional
    public Proposal acceptProposal(Long id) {
        Proposal proposal = proposalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Proposal not found with id: " + id));
        
        if (!Proposal.Status.UNDER_REVIEW.equals(proposal.getStatus()) && 
            !Proposal.Status.NEGOTIATING.equals(proposal.getStatus())) {
            throw new IllegalStateException("Proposal cannot be accepted in current status");
        }
        
        proposal.setStatus(Proposal.Status.ACCEPTED);
        log.info("Proposal {} accepted", proposal.getProposalCode());
        return proposalRepository.save(proposal);
    }
    
    @Transactional
    public Proposal rejectProposal(Long id, String reason) {
        Proposal proposal = proposalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Proposal not found with id: " + id));
        
        if (Proposal.Status.ACCEPTED.equals(proposal.getStatus()) || 
            Proposal.Status.REJECTED.equals(proposal.getStatus()) ||
            Proposal.Status.WITHDRAWN.equals(proposal.getStatus())) {
            throw new IllegalStateException("Proposal cannot be rejected in current status");
        }
        
        proposal.setStatus(Proposal.Status.REJECTED);
        log.info("Proposal {} rejected. Reason: {}", proposal.getProposalCode(), reason);
        return proposalRepository.save(proposal);
    }
    
    @Transactional
    public Proposal withdrawProposal(Long id) {
        Proposal proposal = proposalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Proposal not found with id: " + id));
        
        if (Proposal.Status.ACCEPTED.equals(proposal.getStatus())) {
            throw new IllegalStateException("Accepted proposals cannot be withdrawn");
        }
        
        proposal.setStatus(Proposal.Status.WITHDRAWN);
        log.info("Proposal {} withdrawn by proposer", proposal.getProposalCode());
        return proposalRepository.save(proposal);
    }
    
    @Transactional
    public Proposal startNegotiation(Long id) {
        Proposal proposal = proposalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Proposal not found with id: " + id));
        
        if (!Proposal.Status.UNDER_REVIEW.equals(proposal.getStatus())) {
            throw new IllegalStateException("Only proposals under review can start negotiation");
        }
        
        proposal.setStatus(Proposal.Status.NEGOTIATING);
        log.info("Proposal {} negotiation started", proposal.getProposalCode());
        return proposalRepository.save(proposal);
    }
    
    public boolean canUserSubmitProposal(Project project, User proposer) {
        return Project.Status.IN_BIDDING.equals(project.getStatus()) &&
               !proposalRepository.existsByProjectAndProposer(project, proposer);
    }
    
    public long countByProject(Project project) {
        return proposalRepository.countByProject(project);
    }
    
    public long countByProposer(User proposer) {
        return proposalRepository.countByProposer(proposer);
    }
    
    public long countByStatus(Proposal.Status status) {
        return proposalRepository.countByStatus(status);
    }
    
    public long countBySubmissionDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return proposalRepository.countBySubmissionDateRange(startDate, endDate);
    }
    
    @Transactional
    public void deleteProposal(Long id) {
        Proposal proposal = proposalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Proposal not found with id: " + id));
        
        if (!Proposal.Status.DRAFT.equals(proposal.getStatus()) && 
            !Proposal.Status.WITHDRAWN.equals(proposal.getStatus())) {
            throw new IllegalStateException("Only draft or withdrawn proposals can be deleted");
        }
        
        proposalRepository.deleteById(id);
    }
    
    private String generateProposalCode() {
        String yearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        long count = proposalRepository.count() + 1;
        return String.format("PRP-%s-%03d", yearMonth, count);
    }
    
    private void validateProposalForSubmission(Proposal proposal) {
        if (proposal.getTitle() == null || proposal.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Proposal title is required");
        }
        if (proposal.getContent() == null || proposal.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Proposal content is required");
        }
        if (proposal.getPrice() == null || proposal.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Proposal price must be greater than zero");
        }
        if (proposal.getDeliveryDays() == null || proposal.getDeliveryDays() <= 0) {
            throw new IllegalArgumentException("Delivery days must be greater than zero");
        }
    }
    
    // DTO Support Methods
    
    public Page<ProposalDTO> searchProposals(Long projectId, String status, Pageable pageable) {
        Specification<Proposal> spec = Specification.where(null);
        
        if (projectId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("project").get("id"), projectId));
        }
        
        if (status != null) {
            try {
                Proposal.Status statusEnum = Proposal.Status.valueOf(status.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), statusEnum));
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }
        
        return proposalRepository.findAll(spec, pageable).map(ProposalDTO::fromEntity);
    }
    
    public ProposalDTO getProposalById(Long id) {
        Proposal proposal = proposalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Proposal not found with id: " + id));
        
        return ProposalDTO.fromEntity(proposal);
    }
    
    @Transactional
    public ProposalDTO createProposal(ProposalDTO proposalDTO) {
        // TODO: Get current user from security context
        User currentUser = userRepository.findById(2L).orElse(null); // Temporary
        
        Project project = projectRepository.findById(proposalDTO.getProjectId())
            .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        
        if (!Project.Status.IN_BIDDING.equals(project.getStatus())) {
            throw new IllegalStateException("Project is not accepting proposals");
        }
        
        if (proposalRepository.existsByProjectAndProposer(project, currentUser)) {
            throw new IllegalStateException("You have already submitted a proposal for this project");
        }
        
        Proposal proposal = proposalDTO.toEntity();
        proposal.setProject(project);
        proposal.setProposer(currentUser);
        proposal.setStatus(Proposal.Status.DRAFT);
        
        Proposal savedProposal = proposalRepository.save(proposal);
        return ProposalDTO.fromEntity(savedProposal);
    }
    
    @Transactional
    public ProposalDTO updateProposal(Long id, ProposalDTO proposalDTO) {
        Proposal proposal = proposalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Proposal not found with id: " + id));
        
        if (!Proposal.Status.DRAFT.equals(proposal.getStatus())) {
            throw new IllegalStateException("Only draft proposals can be updated");
        }
        
        // Update fields
        if (proposalDTO.getProposedAmount() != null) {
            proposal.setPrice(proposalDTO.getProposedAmount());
        }
        if (proposalDTO.getEstimatedDuration() != null) {
            proposal.setDeliveryDays(proposalDTO.getEstimatedDuration());
        }
        if (proposalDTO.getContent() != null) {
            proposal.setContent(proposalDTO.getContent());
        }
        if (proposalDTO.getAttachmentFiles() != null) {
            proposal.setAttachmentFiles(proposalDTO.getAttachmentFiles());
        }
        
        Proposal savedProposal = proposalRepository.save(proposal);
        return ProposalDTO.fromEntity(savedProposal);
    }
    
    @Transactional
    public ProposalDTO submitProposalDTO(Long id) {
        Proposal proposal = proposalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Proposal not found with id: " + id));
        
        if (!Proposal.Status.DRAFT.equals(proposal.getStatus())) {
            throw new IllegalStateException("Only draft proposals can be submitted");
        }
        
        // Validate required fields
        if (proposal.getContent() == null || proposal.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content is required");
        }
        if (proposal.getPrice() == null || proposal.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        if (proposal.getDeliveryDays() == null || proposal.getDeliveryDays() <= 0) {
            throw new IllegalArgumentException("Delivery days must be greater than zero");
        }
        
        proposal.setStatus(Proposal.Status.SUBMITTED);
        proposal.setSubmittedAt(LocalDateTime.now());
        
        // Increase project's proposal count
        Project project = proposal.getProject();
        project.setProposalCount(project.getProposalCount() + 1);
        projectRepository.save(project);
        
        Proposal savedProposal = proposalRepository.save(proposal);
        return ProposalDTO.fromEntity(savedProposal);
    }
    
    @Transactional
    public ProposalDTO withdrawProposalDTO(Long id) {
        Proposal proposal = withdrawProposal(id);
        return ProposalDTO.fromEntity(proposal);
    }
    
    private void updateProposalFields(Proposal proposal, Proposal updateData) {
        if (updateData.getTitle() != null) {
            proposal.setTitle(updateData.getTitle());
        }
        if (updateData.getSummary() != null) {
            proposal.setSummary(updateData.getSummary());
        }
        if (updateData.getContent() != null) {
            proposal.setContent(updateData.getContent());
        }
        if (updateData.getApproachMethodology() != null) {
            proposal.setApproachMethodology(updateData.getApproachMethodology());
        }
        if (updateData.getDeliverables() != null) {
            proposal.setDeliverables(updateData.getDeliverables());
        }
        if (updateData.getPrice() != null) {
            proposal.setPrice(updateData.getPrice());
        }
        if (updateData.getDeliveryDays() != null) {
            proposal.setDeliveryDays(updateData.getDeliveryDays());
        }
        if (updateData.getMilestones() != null) {
            proposal.setMilestones(updateData.getMilestones());
        }
        if (updateData.getPaymentTerms() != null) {
            proposal.setPaymentTerms(updateData.getPaymentTerms());
        }
        if (updateData.getWarrantyPeriod() != null) {
            proposal.setWarrantyPeriod(updateData.getWarrantyPeriod());
        }
        if (updateData.getAttachmentFiles() != null) {
            proposal.setAttachmentFiles(updateData.getAttachmentFiles());
        }
    }
    
    public Map<String, Object> getProposalStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Count by status
        Map<String, Long> statusCounts = new HashMap<>();
        for (Proposal.Status status : Proposal.Status.values()) {
            statusCounts.put(status.name().toLowerCase(), countByStatus(status));
        }
        
        stats.put("totalProposals", proposalRepository.count());
        stats.put("statusCounts", statusCounts);
        
        return stats;
    }
}