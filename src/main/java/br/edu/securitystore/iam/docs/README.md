# IAM

O IAM contém três contextos: `identity` (perfil e estado do usuário), `authentication` (credenciais, sessões, refresh tokens e JWT) e `authorization` (roles e permissions). Cada contexto possui API pública, core e infraestrutura próprios. A comunicação entre eles usa `api.module` e adapters; relacionamentos persistidos entre contextos são IDs escalares.

O cadastro cria Identity, AuthenticationAccount e atribui USER em uma transação. Desabilitar Identity revoga suas sessões. Um access token HS256 carrega `sub` (identityId), `sid`, `jti`, `iat`, `exp` e `iss`; o filtro consulta Identity, conta, sessão e authorities atuais. O refresh token é aleatório, armazenado somente como hash SHA-256 e rotacionado sob lock no banco.

Endpoints: `/auth/login`, `/auth/refresh`, `/auth/logout`, `/auth/me`, `/auth/me/sessions` e `/admin/users`, `/admin/roles`, `/admin/permissions` com subrecursos descritos nos controllers. O acesso autenticado utiliza exclusivamente Bearer JWT. `identityId` é a referência entre contextos; e-mail serve ao login e à exibição.

`JWT_SECRET` é obrigatório e deve ser Base64 de pelo menos 32 bytes. O bootstrap do administrador é controlado por `ADMIN_BOOTSTRAP_ENABLED`, `ADMIN_NAME`, `ADMIN_EMAIL` e `ADMIN_PASSWORD`. Sem essas variáveis, nenhum administrador padrão é criado. `security.demo.enabled` fica desligado por padrão.

As páginas em `/login.html` e `/admin/` guardam tokens em `sessionStorage` apenas para a demonstração. Em produção web, avaliar refresh token em cookie `HttpOnly`, `Secure`, com `SameSite` apropriado e proteção CSRF. O banco H2 atual é descartável e recriado a cada inicialização.
