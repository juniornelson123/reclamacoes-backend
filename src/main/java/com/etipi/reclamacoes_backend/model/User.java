package com.etipi.reclamacoes_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String cpf; // CPF como identificador único

    private String name;

    @Column(unique = true)
    private String email;

    private String password; // opcional — use se for implementar autenticação com senha
}
