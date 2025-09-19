package com.hosbee.common.dto;

import com.hosbee.common.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    private String phone;
    private String companyName;
    private String businessType;
    private String profileImage;
    private String introduction;
    private String skills;
    
    private User.Role role;
    private User.Status status;
    
    private Integer projectCount;
    private BigDecimal rating;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .companyName(user.getCompanyName())
                .businessType(user.getBusinessType())
                .profileImage(user.getProfileImage())
                .introduction(user.getIntroduction())
                .skills(user.getSkills())
                .role(user.getRole())
                .status(user.getStatus())
                .projectCount(user.getProjectCount())
                .rating(user.getRating())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    
    public User toEntity() {
        return User.builder()
                .id(this.id)
                .username(this.username)
                .email(this.email)
                .phone(this.phone)
                .companyName(this.companyName)
                .businessType(this.businessType)
                .profileImage(this.profileImage)
                .introduction(this.introduction)
                .skills(this.skills)
                .role(this.role)
                .status(this.status)
                .projectCount(this.projectCount != null ? this.projectCount : 0)
                .rating(this.rating != null ? this.rating : BigDecimal.ZERO)
                .build();
    }
}