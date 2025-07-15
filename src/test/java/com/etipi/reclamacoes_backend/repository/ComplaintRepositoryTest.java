package com.etipi.reclamacoes_backend.repository;

import com.etipi.reclamacoes_backend.enums.ComplaintStatus;
import com.etipi.reclamacoes_backend.model.Complaint;
import com.etipi.reclamacoes_backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ComplaintRepositoryTest {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void deveBuscarReclamacoesPorUsuario() {
        User user = new User();
        user.setName("Joana Silva");
        user.setCpf("11122233344");
        user.setEmail("joana@email.com");
        user.setPassword("senha123");
        userRepository.save(user);

        Complaint complaint = new Complaint();
        complaint.setUser(user);
        complaint.setTitle("Problema no sistema");
        complaint.setDescription("Sistema não está funcionando corretamente");
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setStatus(ComplaintStatus.PENDENTE);
        complaintRepository.save(complaint);

        List<Complaint> lista = complaintRepository.findByUser(user);

        assertThat(lista).isNotEmpty();
        assertThat(lista.get(0).getTitle()).isEqualTo("Problema no sistema");
    }
}
