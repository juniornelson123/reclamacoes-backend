package com.etipi.reclamacoes_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.etipi.reclamacoes_backend.enums.ComplaintStatus;

@Entity
@Data
@NoArgsConstructor
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Agora faz referência ao ID do usuário
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;

    @Column(length = 2000)
    private String description;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status = ComplaintStatus.PENDENTE;
}
