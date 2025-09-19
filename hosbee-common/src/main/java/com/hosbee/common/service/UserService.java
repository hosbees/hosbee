package com.hosbee.common.service;

import com.hosbee.common.dto.UserDTO;
import com.hosbee.common.entity.User;
import com.hosbee.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional
    public User createUser(User user) {
        validateUserData(user);
        return userRepository.save(user);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public Page<User> findByStatus(User.Status status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable);
    }
    
    public Page<User> findByRole(User.Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }
    
    public Page<User> searchByKeyword(String keyword, Pageable pageable) {
        return userRepository.findByKeyword(keyword, pageable);
    }
    
    @Transactional
    public User updateUser(Long id, User updateData) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        updateUserFields(user, updateData);
        return userRepository.save(user);
    }
    
    @Transactional
    public User approveUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setStatus(User.Status.ACTIVE);
        return userRepository.save(user);
    }
    
    @Transactional
    public User suspendUser(Long id, String reason) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setStatus(User.Status.SUSPENDED);
        log.info("User {} suspended. Reason: {}", user.getUsername(), reason);
        return userRepository.save(user);
    }
    
    @Transactional
    public User changeRole(Long id, User.Role newRole) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setRole(newRole);
        log.info("User {} role changed to {}", user.getUsername(), newRole);
        return userRepository.save(user);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public long countByStatus(User.Status status) {
        return userRepository.countByStatus(status);
    }
    
    public long countByRole(User.Role role) {
        return userRepository.countByRole(role);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    private void validateUserData(User user) {
        if (existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
    }
    
    private void updateUserFields(User user, User updateData) {
        if (updateData.getEmail() != null && !updateData.getEmail().equals(user.getEmail())) {
            if (existsByEmail(updateData.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + updateData.getEmail());
            }
            user.setEmail(updateData.getEmail());
        }
        
        if (updateData.getPhone() != null) {
            user.setPhone(updateData.getPhone());
        }
        if (updateData.getCompanyName() != null) {
            user.setCompanyName(updateData.getCompanyName());
        }
        if (updateData.getBusinessType() != null) {
            user.setBusinessType(updateData.getBusinessType());
        }
        if (updateData.getProfileImage() != null) {
            user.setProfileImage(updateData.getProfileImage());
        }
        if (updateData.getIntroduction() != null) {
            user.setIntroduction(updateData.getIntroduction());
        }
        if (updateData.getSkills() != null) {
            user.setSkills(updateData.getSkills());
        }
    }
    
    // DTO Support Methods for Admin
    
    public Page<UserDTO> searchUsers(String role, String status, String keyword, Pageable pageable) {
        Specification<User> spec = Specification.where(null);
        
        if (role != null) {
            try {
                User.Role roleEnum = User.Role.valueOf(role.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("role"), roleEnum));
            } catch (IllegalArgumentException e) {
                // Invalid role, ignore
            }
        }
        
        if (status != null) {
            try {
                User.Status statusEnum = User.Status.valueOf(status.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), statusEnum));
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.or(
                    cb.like(cb.lower(root.get("username")), "%" + keyword.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("email")), "%" + keyword.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("fullName")), "%" + keyword.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("company")), "%" + keyword.toLowerCase() + "%")
                ));
        }
        
        return userRepository.findAll(spec, pageable).map(UserDTO::fromEntity);
    }
    
    @Transactional  
    public UserDTO activateUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        user.setStatus(User.Status.ACTIVE);
        User savedUser = userRepository.save(user);
        return UserDTO.fromEntity(savedUser);
    }
    
    @Transactional
    public UserDTO deactivateUser(Long userId) {
        User user = suspendUser(userId, "Deactivated by admin");
        return UserDTO.fromEntity(user);
    }
    
    @Transactional
    public UserDTO changeUserRole(Long userId, String roleStr) {
        try {
            User.Role role = User.Role.valueOf(roleStr.toUpperCase());
            User user = changeRole(userId, role);
            return UserDTO.fromEntity(user);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + roleStr);
        }
    }
    
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Count by status
        Map<String, Long> statusCounts = new HashMap<>();
        for (User.Status status : User.Status.values()) {
            statusCounts.put(status.name().toLowerCase(), countByStatus(status));
        }
        
        // Count by role
        Map<String, Long> roleCounts = new HashMap<>();
        for (User.Role role : User.Role.values()) {
            roleCounts.put(role.name().toLowerCase(), countByRole(role));
        }
        
        stats.put("totalUsers", userRepository.count());
        stats.put("statusCounts", statusCounts);
        stats.put("roleCounts", roleCounts);
        
        return stats;
    }
}