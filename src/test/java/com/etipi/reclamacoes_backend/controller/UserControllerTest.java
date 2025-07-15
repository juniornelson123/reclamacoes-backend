package com.etipi.reclamacoes_backend.controller;

import com.etipi.reclamacoes_backend.dto.LoginRequest;
import com.etipi.reclamacoes_backend.dto.RegisterRequest;
import com.etipi.reclamacoes_backend.model.User;
import com.etipi.reclamacoes_backend.repository.UserRepository;
import com.etipi.reclamacoes_backend.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void deveRegistrarUsuarioComSucesso() throws Exception {
        RegisterRequest request = new RegisterRequest("João", "messi@email.com", "12345678900", "senha123");

        Mockito.when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("senhaCriptografada");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string("Usuário registrado com sucesso."));
    }

    @Test
    void deveRetornarErroSeEmailDuplicado() throws Exception {
        RegisterRequest request = new RegisterRequest("Maria", "maria@email.com", "12345678901", "senha123");

        Mockito.when(userRepository.findByEmail("maria@email.com"))
            .thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Email já está em uso."));
    }

    @Test
    void deveRetornarErroSeCpfDuplicado() throws Exception {
        RegisterRequest request = new RegisterRequest("Carlos", "carlos@email.com", "12345678902", "senha123");

        Mockito.when(userRepository.findByEmail("carlos@email.com")).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByCpf("12345678902")).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("CPF já está em uso."));
    }

    @Test
    void deveLogarComSucesso() throws Exception {
        LoginRequest request = new LoginRequest("messi@email.com", "senha123");
        User mockUser = new User();
        mockUser.setEmail("messi@email.com");
        mockUser.setPassword("senhaCriptografada");

        Mockito.when(userRepository.findByEmail("messi@email.com")).thenReturn(Optional.of(mockUser));
        Mockito.when(passwordEncoder.matches("senha123", "senhaCriptografada")).thenReturn(true);
        Mockito.when(jwtUtil.generateToken("messi@email.com")).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("fake-jwt-token"))
            .andExpect(jsonPath("$.email").value("messi@email.com"));
    }

    @Test
    void deveRetornarErroLoginComSenhaIncorreta() throws Exception {
        LoginRequest request = new LoginRequest("messi@email.com", "senhaErrada");
        User mockUser = new User();
        mockUser.setPassword("senhaCriptografada");

        Mockito.when(userRepository.findByEmail("messi@email.com")).thenReturn(Optional.of(mockUser));
        Mockito.when(passwordEncoder.matches("senhaErrada", "senhaCriptografada")).thenReturn(false);

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(content().string("Email ou senha inválidos."));
    }

    @Test
    void deveRetornarErroLoginComEmailInexistente() throws Exception {
        LoginRequest request = new LoginRequest("naoexiste@email.com", "qualquer");

        Mockito.when(userRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(content().string("Email ou senha inválidos."));
    }
}
