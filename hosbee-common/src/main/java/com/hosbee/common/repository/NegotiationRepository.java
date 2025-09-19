package com.hosbee.common.repository;

import com.hosbee.common.entity.Negotiation;
import com.hosbee.common.entity.Proposal;
import com.hosbee.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NegotiationRepository extends JpaRepository<Negotiation, Long>, JpaSpecificationExecutor<Negotiation> {
    
    List<Negotiation> findByProposal(Proposal proposal);
    
    List<Negotiation> findByProposalOrderByRoundNumberAsc(Proposal proposal);
    
    Page<Negotiation> findByFromUser(User fromUser, Pageable pageable);
    
    Page<Negotiation> findByToUser(User toUser, Pageable pageable);
    
    Page<Negotiation> findByStatus(Negotiation.Status status, Pageable pageable);
    
    @Query("SELECT n FROM Negotiation n WHERE n.proposal = :proposal AND n.roundNumber = :roundNumber")
    Negotiation findByProposalAndRoundNumber(@Param("proposal") Proposal proposal, @Param("roundNumber") Integer roundNumber);
    
    @Query("SELECT MAX(n.roundNumber) FROM Negotiation n WHERE n.proposal = :proposal")
    Integer findMaxRoundNumberByProposal(@Param("proposal") Proposal proposal);
    
    @Query("SELECT n FROM Negotiation n WHERE (n.fromUser = :user OR n.toUser = :user) AND n.status = :status")
    Page<Negotiation> findByUserAndStatus(@Param("user") User user, @Param("status") Negotiation.Status status, Pageable pageable);
    
    long countByProposal(Proposal proposal);
    
    long countByStatus(Negotiation.Status status);
}