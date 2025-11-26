# AccessManager API

API de gerenciamento de acessos desenvolvida com **Spring Boot**, **Docker** e documentação **Swagger**.

---

## Tecnologias

- Java 17+
- Spring Boot 3
- Spring Security & JWT
- Spring Data JPA / Hibernate
- PostgreSQL
- Docker & Docker Compose
- Swagger / OpenAPI
- JUnit 5 / Mockito para testes

---

## Rodando a aplicação

### 1. Com Docker Compose

docker-compose up --build

Isso irá:

- Subir um container com PostgreSQL
- Construir e subir a API AccessManager
- Disponibilizar a documentação Swagger

### 2. Localmente (sem Docker)

Pré-requisitos:

- Java 21
- Maven
- PostgreSQL

Passos:

1. Configurar o banco de dados no application-dev.yml
2. Rodar o Maven build:

./mvnw clean install

3. Executar a aplicação:

./mvnw spring-boot:run

A API estará disponível em http://localhost:8080

---

## Documentação da API

A documentação Swagger está disponível em:

http://localhost:8080/swagger-ui.html

Ou utilizando o arquivo openapi.yml em src/main/resources/static/openapi.yml

---

## Autenticação

- Autenticação via JWT
- Endpoints de login: POST /auth/login
- Regras de acesso baseadas em papéis (Papel) e departamentos (Departamento)

---

## Testes

Os testes unitários e de integração estão em src/test/java

Rodar todos os testes:

./mvnw test

---

## Scripts de banco

- V1__create_schema.sql – Criação do schema do banco
- V2__insert_initial_data.sql – Inserção de dados iniciais

---

## Funcionalidades principais

- Gerenciamento de usuários, papéis e departamentos
- Solicitação e renovação de acessos a módulos
- Validações customizadas (limite de módulos, compatibilidade, justificativas, etc)
- Segurança com JWT e controle de acesso por papel
