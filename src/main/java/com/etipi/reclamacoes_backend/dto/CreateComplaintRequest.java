package com.etipi.reclamacoes_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateComplaintRequest {

    @NotBlank(message = "O título é obrigatório.")
    private String title;

    @NotBlank(message = "A descrição é obrigatória.")
    private String description;
}
