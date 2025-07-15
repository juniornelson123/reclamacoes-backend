package com.etipi.reclamacoes_backend.controller;

import com.etipi.reclamacoes_backend.model.Complaint;
import com.etipi.reclamacoes_backend.model.User;
import com.etipi.reclamacoes_backend.repository.ComplaintRepository;
import com.etipi.reclamacoes_backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ComplaintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    
    @BeforeEach
    public void setup() {
        complaintRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setName("Maria Teste");
        user.setCpf("12345678900");
        user.setEmail("maria@teste.com");
        user.setPassword("senha123");
        userRepository.save(user);

        // Adiciona o usuário autenticado no SecurityContextHolder
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            user, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void deveCriarReclamacaoComSucesso() throws Exception {
        Complaint nova = new Complaint();
        nova.setTitle("Produto com defeito");
        nova.setDescription("Recebi produto quebrado");
        nova.setCreatedAt(LocalDateTime.now());
        nova.setUser(user);

        mockMvc.perform(post("/api/complaints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nova)))
                .andExpect(status().isOk());
    }

    @Test
    public void deveRejeitarReclamacaoSemTitulo() throws Exception {
        Complaint nova = new Complaint();
        nova.setDescription("Produto veio com defeito");
        nova.setCreatedAt(LocalDateTime.now());
        nova.setUser(user);

        mockMvc.perform(post("/api/complaints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nova)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deveListarMinhasReclamacoes() throws Exception {
        Complaint c = new Complaint();
        c.setTitle("Reclamação 1");
        c.setDescription("Desc");
        c.setCreatedAt(LocalDateTime.now());
        c.setUser(user);
        complaintRepository.save(c);

        mockMvc.perform(get("/api/complaints")
                .param("cpf", user.getCpf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Reclamação 1"));
    }
} 
