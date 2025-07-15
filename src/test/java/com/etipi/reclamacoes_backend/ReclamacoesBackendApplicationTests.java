package com.etipi.reclamacoes_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@OpenAPIDefinition(
    info = @Info(
        title = "API de Reclamações",
        version = "1.0",
        description = "Documentação da API REST para gerenciamento de reclamações"
    )
)
@SpringBootTest
class ReclamacoesBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
