package com.etipi.reclamacoes_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

import com.etipi.reclamacoes_backend.enums.ComplaintStatus;

@Data
public class ComplaintResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private ComplaintStatus status;
}
