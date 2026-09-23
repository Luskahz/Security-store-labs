# Backlog Scrum — Security Store Labs

## Visão do produto

Construir uma loja didática, pequena e reiniciável, para praticar desenvolvimento seguro e testes ofensivos **somente no ambiente autorizado da turma**, usando exclusivamente dados fictícios.

## Definição de pronto

Uma story está pronta quando possui código revisado, teste automatizado essencial, documentação do endpoint, dados sem informação real e uma forma clara de restaurar o laboratório.

## Épico 1 — Fundação

### US-001 — Inicializar o backend (P0) — CONCLUÍDA
Como estudante, quero executar um projeto Spring Boot para evoluir o laboratório. Aceite: Java 21, Maven, H2 e teste de contexto configurados.

### US-002 — Organizar módulos (P0) — CONCLUÍDA
Como desenvolvedor, quero pacotes separados de IAM, catálogo e vendas para localizar responsabilidades facilmente.

### US-003 — Documentar e observar a API (P1)
Como estudante, quero OpenAPI e logs com correlação para entender chamadas sem registrar senhas, tokens ou dados de pagamento.

### US-004 — Padronizar erros (P1) — PARCIAL
Como cliente da API, quero erros JSON consistentes, sem stack traces ou detalhes internos.
Problemas comuns retornam Problem Details; ainda falta cobrir sistematicamente validação e todos os erros de domínio.

## Épico 2 — IAM

### US-101 — Cadastrar usuário fictício (P0) — CONCLUÍDA
Como administrador, quero criar contas com nome, e-mail e senha para o laboratório. Aceite: e-mail único, validação básica e senha com BCrypt.

### US-102 — Autenticar e consultar identidade (P0) — CONCLUÍDA
Como usuário, quero autenticar via `/auth/login` e consultar `/auth/me` com Bearer JWT. Aceite: credenciais e tokens inválidos retornam 401.

### US-103 — Autorizar por papel (P0) — CONCLUÍDA
Como administrador, quero que operações administrativas exijam permissions atribuídas por roles.

### US-104 — Implementar tokens de sessão (P1) — CONCLUÍDA
Como usuário, quero login com token curto e renovação controlada. Aceite: expiração, logout/revogação e segredo fora do repositório.

### US-105 — Recuperar senha de laboratório (P2)
Como usuário, quero redefinir a senha por token fictício, de uso único e expirável, sem envio de e-mail real.

## Épico 3 — Catálogo

### US-201 — Listar produtos (P0) — CONCLUÍDA
Como visitante, quero ver produtos disponíveis com nome, descrição, preço e estoque.

### US-202 — Criar e excluir produtos (P0) — CONCLUÍDA
Como administrador, quero cadastrar e remover produtos. Aceite: validação de preço/estoque e autorização ADMIN.

### US-203 — Atualizar produto e controlar concorrência (P1)
Como administrador, quero editar um produto sem sobrescrever mudanças concorrentes.

### US-204 — Pesquisar e paginar catálogo (P2)
Como visitante, quero filtrar produtos com paginação e limites de tamanho.

## Épico 4 — Venda, pagamento e entrega

### US-301 — Criar pedido (P0) — CONCLUÍDA
Como cliente autenticado, quero comprar uma quantidade disponível de um produto e receber o total calculado no servidor.

### US-302 — Consultar apenas meus pedidos (P0) — CONCLUÍDA
Como cliente, quero listar somente meus pedidos; ADMIN pode consultar todos.

### US-303 — Simular pagamento (P0) — CONCLUÍDA
Como cliente, quero marcar meu pedido como pago usando um método fictício, sem armazenar cartão ou credencial financeira.

### US-304 — Atualizar entrega (P0) — CONCLUÍDA
Como administrador, quero alterar o status logístico de um pedido.

### US-305 — Garantir idempotência e transações (P1) — PARCIAL
Como sistema, quero impedir cobrança repetida e estoque negativo em requisições concorrentes.
Reserva de estoque usa lock pessimista e pagamento mockado pode ser repetido; falta teste de concorrência e chave de idempotência para pedidos.

## Épico 5 — Frontend

### US-401 — Exibir catálogo e identidade (P0) — CONCLUÍDA
Como usuário, quero uma página simples que autentique e liste produtos.

### US-402 — Fluxo completo de compra (P1)
Como cliente, quero cadastrar, entrar, comprar, pagar e acompanhar entrega pela interface.

### US-403 — Administração básica (P2)
Como ADMIN, quero gerir produtos e entregas pela interface.

## Épico 6 — Laboratórios de segurança

### US-501 — Criar perfis seguro e vulnerável (P1)
Como professor, quero ativar desafios isolados por configuração, nunca no perfil padrão. Aceite: aviso visual, documentação e teste que confirme perfil seguro por padrão.

### US-502 — Laboratório de controle de acesso (P1)
Como aluno autorizado, quero investigar uma falha deliberada de acesso a objeto em dados mockados. Aceite: roteiro, objetivo, flag sem segredo real e correção comparável.

### US-503 — Laboratório de injeção (P1)
Como aluno autorizado, quero identificar entrada insegura em endpoint dedicado. Aceite: banco descartável, sem comandos de sistema e sem acesso à rede externa.

### US-504 — Laboratório de autenticação (P2)
Como aluno autorizado, quero comparar políticas fracas e fortes de sessão e limitação de tentativas.

### US-505 — Telemetria e restauração (P1)
Como professor, quero registrar eventos de ataque sem segredos e restaurar todos os dados por script após cada turma.

## Ordem sugerida de sprints

- Sprint 1: US-001, 002, 101–103, 201–202, 301–304, 401.
- Sprint 2: US-003, 004, 104, 203, 305, 402.
- Sprint 3: US-105, 204, 403, 501–505.
