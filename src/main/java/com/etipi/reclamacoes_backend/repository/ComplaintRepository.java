package com.etipi.reclamacoes_backend.repository;

import com.etipi.reclamacoes_backend.model.Complaint;
import com.etipi.reclamacoes_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByUser(User user);
}
