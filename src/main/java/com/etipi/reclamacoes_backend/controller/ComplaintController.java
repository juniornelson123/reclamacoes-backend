package com.etipi.reclamacoes_backend.controller;

import com.etipi.reclamacoes_backend.dto.ComplaintResponse;
import com.etipi.reclamacoes_backend.dto.CreateComplaintRequest;
import com.etipi.reclamacoes_backend.dto.UpdateStatusRequest;
import com.etipi.reclamacoes_backend.enums.ComplaintStatus;
import com.etipi.reclamacoes_backend.model.Complaint;
import com.etipi.reclamacoes_backend.model.User;
import com.etipi.reclamacoes_backend.repository.ComplaintRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.time.LocalDateTime;
import java.util.List;


@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepository;

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping
    public ResponseEntity<ComplaintResponse> createComplaint(@RequestBody @Valid CreateComplaintRequest request) {
        User user = getAuthenticatedUser();

        Complaint complaint = new Complaint();
        complaint.setUser(user);
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setStatus(ComplaintStatus.PENDENTE);

        Complaint saved = complaintRepository.save(complaint);

        return ResponseEntity.ok(toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<ComplaintResponse>> listMyComplaints() {
        User user = getAuthenticatedUser();
        System.out.println("User ID: " + user.getId());
        List<Complaint> complaints = complaintRepository.findByUser(user);
        List<ComplaintResponse> responseList = complaints.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintResponse> getComplaint(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        return complaintRepository.findById(id)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComplaint(@PathVariable Long id, @RequestBody @Valid CreateComplaintRequest request) {
        User user = getAuthenticatedUser();

        return complaintRepository.findById(id)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .map(complaint -> {
                    if (complaint.getStatus() != ComplaintStatus.PENDENTE) {
                        return ResponseEntity.badRequest().body("Reclamação não pode ser editada após o status mudar.");
                    }
                    complaint.setTitle(request.getTitle());
                    complaint.setDescription(request.getDescription());
                    Complaint updated = complaintRepository.save(complaint);
                    return ResponseEntity.ok(toResponse(updated));
                })
                .orElse(ResponseEntity.status(403).body("Acesso negado."));
    }

   @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody @Valid UpdateStatusRequest request) {

        return complaintRepository.findById(id)
                .map(complaint -> {
                    try {
                        ComplaintStatus newStatus = ComplaintStatus.valueOf(request.getStatus().toUpperCase());
                        complaint.setStatus(newStatus);
                        Complaint updated = complaintRepository.save(complaint);
                        return ResponseEntity.ok(toResponse(updated));
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body("Status inválido.");
                    }
                })
                .orElse(ResponseEntity.status(403).body("Acesso negado."));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComplaint(@PathVariable Long id) {
        User user = getAuthenticatedUser();

        return complaintRepository.findById(id)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .map(complaint -> {
                    if (complaint.getStatus() != ComplaintStatus.PENDENTE) {
                        return ResponseEntity.badRequest().body("Reclamação não pode ser excluída após o status mudar.");
                    }
                    complaintRepository.delete(complaint);
                    return ResponseEntity.ok("Reclamação excluída com sucesso.");
                })
                .orElse(ResponseEntity.status(403).body("Acesso negado."));
    }

    private ComplaintResponse toResponse(Complaint complaint) {
        ComplaintResponse dto = new ComplaintResponse();
        dto.setId(complaint.getId());
        dto.setTitle(complaint.getTitle());
        dto.setDescription(complaint.getDescription());
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setStatus(complaint.getStatus());
        return dto;
    }
}
