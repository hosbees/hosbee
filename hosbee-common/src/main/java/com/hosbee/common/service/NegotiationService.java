package com.hosbee.common.service;

import com.hosbee.common.dto.NegotiationDTO;
import com.hosbee.common.entity.Negotiation;
import com.hosbee.common.entity.Proposal;
import com.hosbee.common.entity.User;
import com.hosbee.common.repository.NegotiationRepository;
import com.hosbee.common.repository.ProposalRepository;
import com.hosbee.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NegotiationService {
    
    private final NegotiationRepository negotiationRepository;
    private final ProposalRepository proposalRepository;
    private final UserRepository userRepository;
    
    public Page<NegotiationDTO> searchNegotiations(Long proposalId, String status, Pageable pageable) {
        Specification<Negotiation> spec = Specification.where(null);
        
        if (proposalId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("proposal").get("id"), proposalId));
        }
        
        if (status != null) {
            try {
                Negotiation.Status statusEnum = Negotiation.Status.valueOf(status.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), statusEnum));
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }
        
        return negotiationRepository.findAll(spec, pageable).map(NegotiationDTO::fromEntity);
    }
    
    public NegotiationDTO getNegotiationById(Long id) {
        Negotiation negotiation = negotiationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Negotiation not found with id: " + id));
        
        return NegotiationDTO.fromEntity(negotiation);
    }
    
    @Transactional
    public NegotiationDTO createNegotiation(NegotiationDTO negotiationDTO) {
        // TODO: Get current user from security context
        User currentUser = userRepository.findById(1L).orElse(null); // Temporary
        
        Proposal proposal = proposalRepository.findById(negotiationDTO.getProposalId())
            .orElseThrow(() -> new IllegalArgumentException("Proposal not found"));
        
        if (!Proposal.Status.SUBMITTED.equals(proposal.getStatus()) && 
            !Proposal.Status.UNDER_REVIEW.equals(proposal.getStatus())) {
            throw new IllegalStateException("Cannot negotiate on proposal with current status");
        }
        
        Negotiation negotiation = negotiationDTO.toEntity();
        negotiation.setProposal(proposal);
        negotiation.setFromUser(currentUser);
        negotiation.setStatus(Negotiation.Status.PENDING);
        negotiation.setRoundNumber(1);
        
        // Update proposal status to negotiating
        proposal.setStatus(Proposal.Status.NEGOTIATING);
        proposalRepository.save(proposal);
        
        Negotiation savedNegotiation = negotiationRepository.save(negotiation);
        return NegotiationDTO.fromEntity(savedNegotiation);
    }
    
    @Transactional
    public NegotiationDTO respondToNegotiation(Long id, NegotiationDTO responseDTO) {
        Negotiation originalNegotiation = negotiationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Negotiation not found with id: " + id));
        
        if (!Negotiation.Status.PENDING.equals(originalNegotiation.getStatus())) {
            throw new IllegalStateException("Can only respond to pending negotiations");
        }
        
        // TODO: Get current user from security context
        User currentUser = userRepository.findById(2L).orElse(null); // Temporary
        
        // Create response negotiation
        Negotiation response = responseDTO.toEntity();
        response.setProposal(originalNegotiation.getProposal());
        response.setFromUser(currentUser);
        response.setStatus(Negotiation.Status.COUNTERED);
        response.setRoundNumber(originalNegotiation.getRoundNumber() + 1);
        
        // Mark original as responded
        originalNegotiation.setStatus(Negotiation.Status.COUNTERED);
        negotiationRepository.save(originalNegotiation);
        
        Negotiation savedResponse = negotiationRepository.save(response);
        return NegotiationDTO.fromEntity(savedResponse);
    }
    
    @Transactional
    public NegotiationDTO acceptNegotiation(Long id) {
        Negotiation negotiation = negotiationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Negotiation not found with id: " + id));
        
        if (!Negotiation.Status.PENDING.equals(negotiation.getStatus())) {
            throw new IllegalStateException("Can only accept pending negotiations");
        }
        
        negotiation.setStatus(Negotiation.Status.ACCEPTED);
        
        // Update proposal with agreed terms
        Proposal proposal = negotiation.getProposal();
        proposal.setPrice(negotiation.getPrice());
        proposal.setDeliveryDays(negotiation.getDeliveryDays());
        proposal.setStatus(Proposal.Status.ACCEPTED);
        proposalRepository.save(proposal);
        
        Negotiation savedNegotiation = negotiationRepository.save(negotiation);
        log.info("Negotiation {} accepted for proposal {}", id, proposal.getProposalCode());
        
        return NegotiationDTO.fromEntity(savedNegotiation);
    }
    
    @Transactional
    public NegotiationDTO rejectNegotiation(Long id, String reason) {
        Negotiation negotiation = negotiationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Negotiation not found with id: " + id));
        
        if (!Negotiation.Status.PENDING.equals(negotiation.getStatus())) {
            throw new IllegalStateException("Can only reject pending negotiations");
        }
        
        negotiation.setStatus(Negotiation.Status.REJECTED);
        
        Negotiation savedNegotiation = negotiationRepository.save(negotiation);
        log.info("Negotiation {} rejected for proposal {}. Reason: {}", 
                id, negotiation.getProposal().getProposalCode(), reason);
        
        return NegotiationDTO.fromEntity(savedNegotiation);
    }
    
    @Transactional
    public NegotiationDTO createCounterOffer(Long id, NegotiationDTO counterOfferDTO) {
        Negotiation originalNegotiation = negotiationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Negotiation not found with id: " + id));
        
        if (!Negotiation.Status.PENDING.equals(originalNegotiation.getStatus())) {
            throw new IllegalStateException("Can only create counter offer for pending negotiations");
        }
        
        // TODO: Get current user from security context
        User currentUser = userRepository.findById(2L).orElse(null); // Temporary
        
        // Create counter offer
        Negotiation counterOffer = counterOfferDTO.toEntity();
        counterOffer.setProposal(originalNegotiation.getProposal());
        counterOffer.setFromUser(currentUser);
        counterOffer.setStatus(Negotiation.Status.COUNTERED);
        counterOffer.setRoundNumber(originalNegotiation.getRoundNumber() + 1);
        
        // Mark original as countered
        originalNegotiation.setStatus(Negotiation.Status.COUNTERED);
        negotiationRepository.save(originalNegotiation);
        
        Negotiation savedCounterOffer = negotiationRepository.save(counterOffer);
        log.info("Counter offer created for negotiation {} on proposal {}", 
                id, originalNegotiation.getProposal().getProposalCode());
        
        return NegotiationDTO.fromEntity(savedCounterOffer);
    }
}