package com.hosbee.user.controller;

import com.hosbee.common.dto.NegotiationDTO;
import com.hosbee.common.service.NegotiationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/negotiations")
@RequiredArgsConstructor
public class NegotiationController {

    private final NegotiationService negotiationService;

    @GetMapping
    public ResponseEntity<Page<NegotiationDTO>> getNegotiations(
            @RequestParam(required = false) Long proposalId,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        
        Page<NegotiationDTO> negotiations = negotiationService.searchNegotiations(proposalId, status, pageable);
        return ResponseEntity.ok(negotiations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NegotiationDTO> getNegotiation(@PathVariable Long id) {
        NegotiationDTO negotiation = negotiationService.getNegotiationById(id);
        return ResponseEntity.ok(negotiation);
    }

    @PostMapping
    public ResponseEntity<NegotiationDTO> createNegotiation(@Valid @RequestBody NegotiationDTO negotiationDTO) {
        NegotiationDTO createdNegotiation = negotiationService.createNegotiation(negotiationDTO);
        return ResponseEntity.ok(createdNegotiation);
    }

    @PostMapping("/{id}/respond")
    public ResponseEntity<NegotiationDTO> respondToNegotiation(
            @PathVariable Long id, 
            @Valid @RequestBody NegotiationDTO responseDTO) {
        NegotiationDTO response = negotiationService.respondToNegotiation(id, responseDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<NegotiationDTO> acceptNegotiation(@PathVariable Long id) {
        NegotiationDTO acceptedNegotiation = negotiationService.acceptNegotiation(id);
        return ResponseEntity.ok(acceptedNegotiation);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<NegotiationDTO> rejectNegotiation(
            @PathVariable Long id, 
            @RequestParam String reason) {
        NegotiationDTO rejectedNegotiation = negotiationService.rejectNegotiation(id, reason);
        return ResponseEntity.ok(rejectedNegotiation);
    }

    @PostMapping("/{id}/counter")
    public ResponseEntity<NegotiationDTO> createCounterOffer(
            @PathVariable Long id, 
            @Valid @RequestBody NegotiationDTO counterOfferDTO) {
        NegotiationDTO counterOffer = negotiationService.createCounterOffer(id, counterOfferDTO);
        return ResponseEntity.ok(counterOffer);
    }
}