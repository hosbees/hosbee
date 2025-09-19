package com.hosbee.common.service;

import com.hosbee.common.dto.ProjectDTO;
import com.hosbee.common.entity.Project;
import com.hosbee.common.entity.User;
import com.hosbee.common.repository.ProjectRepository;
import com.hosbee.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public Project createProject(Project project, Long clientId) {
        User client = userRepository.findById(clientId)
            .orElseThrow(() -> new IllegalArgumentException("Client not found with id: " + clientId));
        
        project.setClient(client);
        project.setCreatedBy(client);
        project.setProjectCode(generateProjectCode());
        project.setStatus(Project.Status.DRAFT);
        
        return projectRepository.save(project);
    }
    
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }
    
    public Optional<Project> findByProjectCode(String projectCode) {
        return projectRepository.findByProjectCode(projectCode);
    }
    
    public Page<Project> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }
    
    public Page<Project> findByStatus(Project.Status status, Pageable pageable) {
        return projectRepository.findByStatus(status, pageable);
    }
    
    public Page<Project> findByCategory(Project.Category category, Pageable pageable) {
        return projectRepository.findByCategory(category, pageable);
    }
    
    public Page<Project> findByClient(User client, Pageable pageable) {
        return projectRepository.findByClient(client, pageable);
    }
    
    public Page<Project> searchByKeyword(String keyword, Pageable pageable) {
        return projectRepository.findByKeyword(keyword, pageable);
    }
    
    public Page<Project> findActiveBiddingProjects(Pageable pageable) {
        return projectRepository.findActiveBiddingProjects(Project.Status.IN_BIDDING, LocalDateTime.now(), pageable);
    }
    
    @Transactional
    public Project updateProject(Long id, Project updateData) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
        
        updateProjectFields(project, updateData);
        return projectRepository.save(project);
    }
    
    @Transactional
    public Project publishProject(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
        
        if (!Project.Status.DRAFT.equals(project.getStatus())) {
            throw new IllegalStateException("Only draft projects can be published");
        }
        
        project.setStatus(Project.Status.PUBLISHED);
        log.info("Project {} published", project.getProjectCode());
        return projectRepository.save(project);
    }
    
    @Transactional
    public Project startBidding(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
        
        if (!Project.Status.PUBLISHED.equals(project.getStatus())) {
            throw new IllegalStateException("Only published projects can start bidding");
        }
        
        project.setStatus(Project.Status.IN_BIDDING);
        if (project.getBiddingDeadline() == null) {
            project.setBiddingDeadline(LocalDateTime.now().plusDays(7));
        }
        
        log.info("Project {} bidding started", project.getProjectCode());
        return projectRepository.save(project);
    }
    
    @Transactional
    public Project awardProject(Long id, Long winnerId) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
        
        User winner = userRepository.findById(winnerId)
            .orElseThrow(() -> new IllegalArgumentException("Winner not found with id: " + winnerId));
        
        if (!Project.Status.IN_BIDDING.equals(project.getStatus())) {
            throw new IllegalStateException("Only bidding projects can be awarded");
        }
        
        project.setStatus(Project.Status.AWARDED);
        log.info("Project {} awarded to user {}", project.getProjectCode(), winner.getUsername());
        return projectRepository.save(project);
    }
    
    @Transactional
    public Project completeProject(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
        
        project.setStatus(Project.Status.COMPLETED);
        log.info("Project {} completed", project.getProjectCode());
        return projectRepository.save(project);
    }
    
    @Transactional
    public Project cancelProject(Long id, String reason) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
        
        project.setStatus(Project.Status.CANCELLED);
        log.info("Project {} cancelled. Reason: {}", project.getProjectCode(), reason);
        return projectRepository.save(project);
    }
    
    @Transactional
    public Project increaseViewCount(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
        
        project.setViewCount(project.getViewCount() + 1);
        return projectRepository.save(project);
    }
    
    @Transactional
    public Project increaseProposalCount(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
        
        project.setProposalCount(project.getProposalCount() + 1);
        return projectRepository.save(project);
    }
    
    public long countByStatus(Project.Status status) {
        return projectRepository.countByStatus(status);
    }
    
    public long countByCategory(Project.Category category) {
        return projectRepository.countByCategory(category);
    }
    
    public long countByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return projectRepository.countByDateRange(startDate, endDate);
    }
    
    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
        
        if (!Project.Status.DRAFT.equals(project.getStatus()) && !Project.Status.CANCELLED.equals(project.getStatus())) {
            throw new IllegalStateException("Only draft or cancelled projects can be deleted");
        }
        
        projectRepository.deleteById(id);
    }
    
    private String generateProjectCode() {
        String yearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        long count = projectRepository.count() + 1;
        return String.format("PRJ-%s-%03d", yearMonth, count);
    }
    
    // DTO Support Methods
    
    public Page<ProjectDTO> searchProjects(String status, String category, String keyword, Pageable pageable) {
        Specification<Project> spec = Specification.where(null);
        
        if (status != null) {
            try {
                Project.Status statusEnum = Project.Status.valueOf(status.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), statusEnum));
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }
        
        if (category != null) {
            try {
                Project.Category categoryEnum = Project.Category.valueOf(category.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), categoryEnum));
            } catch (IllegalArgumentException e) {
                // Invalid category, ignore
            }
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + keyword.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("requirements")), "%" + keyword.toLowerCase() + "%")
                ));
        }
        
        return projectRepository.findAll(spec, pageable).map(ProjectDTO::fromEntity);
    }
    
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
        
        // Increase view count
        project.setViewCount(project.getViewCount() + 1);
        projectRepository.save(project);
        
        return ProjectDTO.fromEntity(project);
    }
    
    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        // TODO: Get current user from security context
        User currentUser = userRepository.findById(1L).orElse(null); // Temporary
        
        Project project = projectDTO.toEntity();
        project.setClient(currentUser);
        project.setCreatedBy(currentUser);
        project.setProjectCode(generateProjectCode());
        project.setStatus(Project.Status.DRAFT);
        project.setViewCount(0);
        project.setProposalCount(0);
        
        Project savedProject = projectRepository.save(project);
        return ProjectDTO.fromEntity(savedProject);
    }
    
    @Transactional
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
        
        // Update only allowed fields
        if (projectDTO.getTitle() != null) {
            project.setTitle(projectDTO.getTitle());
        }
        if (projectDTO.getDescription() != null) {
            project.setDescription(projectDTO.getDescription());
        }
        if (projectDTO.getRequirements() != null) {
            project.setRequirements(projectDTO.getRequirements());
        }
        if (projectDTO.getCategory() != null) {
            project.setCategory(projectDTO.getCategory());
        }
        if (projectDTO.getPriority() != null) {
            project.setPriority(projectDTO.getPriority());
        }
        if (projectDTO.getBudgetMin() != null) {
            project.setBudgetMin(projectDTO.getBudgetMin());
        }
        if (projectDTO.getBudgetMax() != null) {
            project.setBudgetMax(projectDTO.getBudgetMax());
        }
        if (projectDTO.getDeadline() != null) {
            project.setDeadline(projectDTO.getDeadline());
        }
        if (projectDTO.getBiddingDeadline() != null) {
            project.setBiddingDeadline(projectDTO.getBiddingDeadline());
        }
        if (projectDTO.getRequiredSkills() != null) {
            project.setRequiredSkills(projectDTO.getRequiredSkills());
        }
        if (projectDTO.getAttachmentFiles() != null) {
            project.setAttachmentFiles(projectDTO.getAttachmentFiles());
        }
        
        Project savedProject = projectRepository.save(project);
        return ProjectDTO.fromEntity(savedProject);
    }
    
    @Transactional
    public ProjectDTO publishProjectDTO(Long id) {
        Project project = publishProject(id);
        return ProjectDTO.fromEntity(project);
    }
    
    @Transactional
    public ProjectDTO closeProjectDTO(Long id) {
        Project project = cancelProject(id, "Closed by client");
        return ProjectDTO.fromEntity(project);
    }
    
    private void updateProjectFields(Project project, Project updateData) {
        if (updateData.getTitle() != null) {
            project.setTitle(updateData.getTitle());
        }
        if (updateData.getDescription() != null) {
            project.setDescription(updateData.getDescription());
        }
        if (updateData.getRequirements() != null) {
            project.setRequirements(updateData.getRequirements());
        }
        if (updateData.getCategory() != null) {
            project.setCategory(updateData.getCategory());
        }
        if (updateData.getPriority() != null) {
            project.setPriority(updateData.getPriority());
        }
        if (updateData.getBudgetMin() != null) {
            project.setBudgetMin(updateData.getBudgetMin());
        }
        if (updateData.getBudgetMax() != null) {
            project.setBudgetMax(updateData.getBudgetMax());
        }
        if (updateData.getDeadline() != null) {
            project.setDeadline(updateData.getDeadline());
        }
        if (updateData.getBiddingDeadline() != null) {
            project.setBiddingDeadline(updateData.getBiddingDeadline());
        }
        if (updateData.getRequiredSkills() != null) {
            project.setRequiredSkills(updateData.getRequiredSkills());
        }
        if (updateData.getAttachmentFiles() != null) {
            project.setAttachmentFiles(updateData.getAttachmentFiles());
        }
    }
    
    public Map<String, Object> getProjectStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Count by status
        Map<String, Long> statusCounts = new HashMap<>();
        for (Project.Status status : Project.Status.values()) {
            statusCounts.put(status.name().toLowerCase(), countByStatus(status));
        }
        
        // Count by category
        Map<String, Long> categoryCounts = new HashMap<>();
        for (Project.Category category : Project.Category.values()) {
            categoryCounts.put(category.name().toLowerCase(), countByCategory(category));
        }
        
        stats.put("totalProjects", projectRepository.count());
        stats.put("statusCounts", statusCounts);
        stats.put("categoryCounts", categoryCounts);
        
        return stats;
    }
}