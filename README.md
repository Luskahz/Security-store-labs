# Security Store Labs

Loja full stack mínima para aulas **autorizadas** de cibersegurança. O projeto usa Spring Boot, Java 21, H2 e uma interface estática em HTML/CSS/JS.

## Executar localmente

Com Java 21 e Maven instalados, execute na raiz do projeto. Exemplo em Bash:

```bash
export JWT_SECRET="$(openssl rand -base64 32)"
export ADMIN_BOOTSTRAP_ENABLED=true
export ADMIN_NAME="Administrador do Lab"
export ADMIN_EMAIL="admin@lab.local"
export ADMIN_PASSWORD="defina-uma-senha-forte"
mvn spring-boot:run
```

Abra `http://localhost:8080`. O banco H2 é em memória e reinicia ao parar a aplicação. O registro público cria contas com role USER; o primeiro acesso administrativo só é provisionado se as variáveis de bootstrap forem definidas antes da inicialização.

Exemplo em PowerShell:

```powershell
$env:JWT_SECRET = [Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
$env:ADMIN_BOOTSTRAP_ENABLED = 'true'
$env:ADMIN_NAME = 'Administrador do Lab'
$env:ADMIN_EMAIL = 'admin@lab.local'
$env:ADMIN_PASSWORD = '<defina uma senha forte>'
mvn spring-boot:run
```

O login e cadastro ficam em `http://localhost:8080/login.html`; a administração aparece para contas com permissions. O console H2 fica desabilitado. Todos os dados são fictícios e recriados a cada execução.

## API inicial

- `POST /auth/login` e `POST /auth/refresh` — acesso por Bearer JWT
- `POST /admin/users/register` — cadastro público com role USER
- `GET /auth/me` — identidade autenticada
- `/admin/users`, `/admin/roles`, `/admin/permissions` — gestão IAM
- `GET /api/products` — catálogo público
- `POST/DELETE /api/products/**` — administração de produtos
- `POST /api/orders` e `GET /api/orders` — compras do usuário
- `POST /api/orders/{id}/pay` — pagamento fictício
- `GET /api/deliveries/orders/{orderId}` e `PATCH /api/deliveries/orders/{orderId}` — consulta e atualização do fluxo logístico por permissões próprias
- `POST/DELETE /api/products/**` — gestão administrativa pelo painel em `/admin/products.html`
- painel de logística em `/admin/deliveries.html`; operações de identidade e autorização em `/admin/*.html`

Veja o planejamento em [`docs/backlog.md`](docs/backlog.md) e as regras do laboratório em [`docs/lab-rules.md`](docs/lab-rules.md).
O estado das branches remotas está registrado em [`docs/repository-branches.md`](docs/repository-branches.md).

## Arquitetura

O backend é um monólito modular com os contextos [`iam`](src/main/java/br/edu/securitystore/iam/docs/README.md), [`catalog`](src/main/java/br/edu/securitystore/catalog/docs/README.md), [`sales`](src/main/java/br/edu/securitystore/sales/docs/README.md) e [`logistics`](src/main/java/br/edu/securitystore/logistics/docs/README.md). O IAM se subdivide em Identity, Authentication e Authorization. Cada contexto separa API HTTP, aplicação/domínio, contratos de persistência e adapters. Vendas conversa com logística pelos contratos `api.module`; configurações de segurança e erros comuns ficam em `platform`.
