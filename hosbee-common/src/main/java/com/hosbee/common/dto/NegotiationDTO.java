package com.hosbee.common.dto;

import com.hosbee.common.entity.Negotiation;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NegotiationDTO {
    
    private Long id;
    
    @NotNull(message = "Proposal ID is required")
    private Long proposalId;
    private String proposalCode;
    
    @NotNull(message = "From user ID is required")
    private Long fromUserId;
    private String fromUserName;
    
    @NotNull(message = "To user ID is required")
    private Long toUserId;
    private String toUserName;
    
    private Integer roundNumber;
    
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @Positive(message = "Delivery days must be positive")  
    private Integer deliveryDays;
    
    @NotBlank(message = "Message is required")
    private String message;
    
    private Negotiation.Status status;
    
    private LocalDateTime createdAt;
    
    public static NegotiationDTO fromEntity(Negotiation negotiation) {
        return NegotiationDTO.builder()
                .id(negotiation.getId())
                .proposalId(negotiation.getProposal() != null ? negotiation.getProposal().getId() : null)
                .proposalCode(negotiation.getProposal() != null ? negotiation.getProposal().getProposalCode() : null)
                .fromUserId(negotiation.getFromUser() != null ? negotiation.getFromUser().getId() : null)
                .fromUserName(negotiation.getFromUser() != null ? negotiation.getFromUser().getUsername() : null)
                .toUserId(negotiation.getToUser() != null ? negotiation.getToUser().getId() : null)
                .toUserName(negotiation.getToUser() != null ? negotiation.getToUser().getUsername() : null)
                .roundNumber(negotiation.getRoundNumber())
                .price(negotiation.getPrice())
                .deliveryDays(negotiation.getDeliveryDays())
                .message(negotiation.getMessage())
                .status(negotiation.getStatus())
                .createdAt(negotiation.getCreatedAt())
                .build();
    }
    
    public Negotiation toEntity() {
        return Negotiation.builder()
                .id(this.id)
                .roundNumber(this.roundNumber)
                .price(this.price)
                .deliveryDays(this.deliveryDays)
                .message(this.message)
                .status(this.status)
                .build();
    }
}