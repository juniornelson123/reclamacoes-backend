package com.etipi.reclamacoes_backend.repository;

import com.etipi.reclamacoes_backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setName("João da Silva");
        user.setCpf("99988877766");
        user.setEmail("joao@email.com");
        user.setPassword("senha123");
        userRepository.save(user);
    }

    @Test
    public void deveEncontrarUsuarioPorEmail() {
        Optional<User> encontrado = userRepository.findByEmail("joao@email.com");
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getCpf()).isEqualTo("99988877766");
    }

    @Test
    public void deveEncontrarUsuarioPorCpf() {
        Optional<User> encontrado = userRepository.findByCpf("99988877766");
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    public void naoDeveEncontrarUsuarioPorEmailInexistente() {
        Optional<User> encontrado = userRepository.findByEmail("naoexiste@email.com");
        assertThat(encontrado).isNotPresent();
    }

    @Test
    public void naoDeveEncontrarUsuarioPorCpfInexistente() {
        Optional<User> encontrado = userRepository.findByCpf("00011122233");
        assertThat(encontrado).isNotPresent();
    }
}
