# Reclamações Backend

API REST para gerenciamento de reclamações de usuários. Desenvolvida com Spring Boot, JWT, PostgreSQL, Swagger e Docker.

---

# DER

[Acesse o DER](https://drive.google.com/file/d/1ab8x9n3n_0RTM7Tyz_TGzU3ilzAilSZk/view?usp=sharing)

---

## 🚀 Tecnologias

- Java 17
- Spring Boot 3.2.5
- Spring Security
- JWT (Autenticação)
- PostgreSQL
- Docker
- Swagger OpenAPI
- JPA / Hibernate
- Lombok

---

## 🐳 Rodando com Docker

```bash
# Build da imagem
docker-compose up --build
```

## 📄 Documentação

http://localhost:8080/swagger-ui/index.html

## 🔐 Autenticação

Para acessar endpoints protegidos, é necessário realizar login e obter um token JWT.

**Endpoints públicos:**

- `POST /api/users/register`
- `POST /api/users/login`

---

## 🧪 Testes

```bash
./mvnw test
```

## 📝 Autor

Desenvolvido por Nelson Junior.


## 📄 Licença

Este projeto está sob a licença MIT.