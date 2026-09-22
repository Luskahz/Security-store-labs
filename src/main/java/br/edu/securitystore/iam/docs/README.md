# IAM

Responsável por cadastro e consulta de identidade. O HTTP Basic usa contas deste contexto, com senhas BCrypt. `IdentityQuery` é a API interna; publica identificação e, para a infraestrutura de autenticação, dados de credencial. Outros contextos não acessam a entidade nem o repositório JPA.

Fluxos HTTP: `POST /api/iam/register` e `GET /api/iam/me`. `IamService` executa cadastro e consulta; `UserRepository` é o contrato de persistência do core, implementado em `infra.persistence`. Os dados de demonstração são carregados em memória a cada início. O retorno HTTP não contém o hash da senha.
