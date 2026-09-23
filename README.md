# Security Store Labs

Loja full stack mínima para aulas **autorizadas** de cibersegurança. O projeto usa Spring Boot, Java 21, H2 e uma interface estática em HTML/CSS/JS.

## Executar

Acesse `http://localhost:8080`. Configure as variáveis antes de iniciar (exemplo em PowerShell):

```powershell
$env:JWT_SECRET = [Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
$env:ADMIN_BOOTSTRAP_ENABLED = 'true'
$env:ADMIN_NAME = 'Administrador do Lab'
$env:ADMIN_EMAIL = 'admin@lab.local'
$env:ADMIN_PASSWORD = '<defina uma senha forte>'
mvn spring-boot:run
```

O painel IAM fica em `http://localhost:8080/login.html`. O console H2 fica desabilitado. Todos os dados são mockados e recriados a cada execução.

## API inicial

- `POST /api/iam/register` — cadastro
- `GET /api/iam/me` — identidade autenticada
- `GET /api/products` — catálogo público
- `POST/DELETE /api/products/**` — administração de produtos
- `POST /api/orders` e `GET /api/orders` — compras do usuário
- `POST /api/orders/{id}/pay` — pagamento fictício
- `PATCH /api/orders/{id}/delivery` — atualização de entrega por ADMIN

Veja o planejamento em [`docs/backlog.md`](docs/backlog.md) e as regras do laboratório em [`docs/lab-rules.md`](docs/lab-rules.md).

## Arquitetura

O backend é um monólito modular com os contextos [`iam`](src/main/java/br/edu/securitystore/iam/docs/README.md), [`catalog`](src/main/java/br/edu/securitystore/catalog/docs/README.md) e [`sales`](src/main/java/br/edu/securitystore/sales/docs/README.md). O IAM se subdivide em Identity, Authentication e Authorization. Cada contexto separa API HTTP, aplicação/domínio, contratos de persistência e adapters. Vendas acessa outros contextos somente por ports e `api.module`. Configurações de segurança e erros comuns ficam em `platform`.
