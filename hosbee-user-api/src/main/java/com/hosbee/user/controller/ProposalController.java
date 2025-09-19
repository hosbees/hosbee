package com.hosbee.user.controller;

import com.hosbee.common.dto.ProposalDTO;
import com.hosbee.common.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    @GetMapping
    public ResponseEntity<Page<ProposalDTO>> getProposals(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        
        Page<ProposalDTO> proposals = proposalService.searchProposals(projectId, status, pageable);
        return ResponseEntity.ok(proposals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProposalDTO> getProposal(@PathVariable Long id) {
        ProposalDTO proposal = proposalService.getProposalById(id);
        return ResponseEntity.ok(proposal);
    }

    @PostMapping
    public ResponseEntity<ProposalDTO> createProposal(@Valid @RequestBody ProposalDTO proposalDTO) {
        ProposalDTO createdProposal = proposalService.createProposal(proposalDTO);
        return ResponseEntity.ok(createdProposal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProposalDTO> updateProposal(
            @PathVariable Long id, 
            @Valid @RequestBody ProposalDTO proposalDTO) {
        ProposalDTO updatedProposal = proposalService.updateProposal(id, proposalDTO);
        return ResponseEntity.ok(updatedProposal);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ProposalDTO> submitProposal(@PathVariable Long id) {
        ProposalDTO submittedProposal = proposalService.submitProposalDTO(id);
        return ResponseEntity.ok(submittedProposal);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<ProposalDTO> withdrawProposal(@PathVariable Long id) {
        ProposalDTO withdrawnProposal = proposalService.withdrawProposalDTO(id);
        return ResponseEntity.ok(withdrawnProposal);
    }
}