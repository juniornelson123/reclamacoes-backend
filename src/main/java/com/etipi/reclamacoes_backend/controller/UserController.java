package com.etipi.reclamacoes_backend.controller;

import com.etipi.reclamacoes_backend.dto.LoginRequest;
import com.etipi.reclamacoes_backend.dto.RegisterRequest;
import com.etipi.reclamacoes_backend.model.User;
import com.etipi.reclamacoes_backend.repository.UserRepository;
import com.etipi.reclamacoes_backend.security.JwtUtil;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email já está em uso.");
        }

        if (userRepository.findByCpf(request.getCpf()).isPresent()) {
            return ResponseEntity.badRequest().body("CPF já está em uso.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setCpf(request.getCpf());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        return ResponseEntity.ok("Usuário registrado com sucesso.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(401).body("Email ou senha inválidos.");
        }

        String token = jwtUtil.generateToken(request.getEmail());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", request.getEmail()
        ));
    }
}
