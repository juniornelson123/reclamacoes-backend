package com.etipi.reclamacoes_backend.handler;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveTratarIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("CPF inválido");
        WebRequest request = mock(WebRequest.class);

        ResponseEntity<?> response = handler.handleIllegalArgument(exception, request);

        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("CPF inválido", body.get("message"));
    }

    @Test
    void deveTratarRuntimeException() {
        RuntimeException exception = new RuntimeException("Falha interna");
        WebRequest request = mock(WebRequest.class);

        ResponseEntity<?> response = handler.handleRuntime(exception, request);

        assertEquals(500, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Erro inesperado", body.get("message"));
        assertEquals("Falha interna", body.get("error"));
    }

    @Test
    void deveTratarMethodArgumentNotValidException() throws Exception {
        // Simula a exceção de validação de argumentos
        Method method = SampleController.class.getMethod("sampleMethod", SampleDto.class);
        MethodArgumentNotValidException exception =
            new MethodArgumentNotValidException(null, new SampleBindingResult());

        ResponseEntity<?> response = handler.handleValidationExceptions(exception);

        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Erro de validação", body.get("message"));
        assertTrue(body.containsKey("errors"));
    }

    // Classes auxiliares fictícias para simular validação
    static class SampleController {
        public void sampleMethod(SampleDto dto) {}
    }

    @Data
    static class SampleDto {
        public String name;
    }

    static class SampleBindingResult extends org.springframework.validation.BeanPropertyBindingResult {
        public SampleBindingResult() {
            super(new SampleDto(), "sampleDto");
            rejectValue("name", "NotBlank", "Nome é obrigatório");
        }
    }
}
