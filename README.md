# Security Store Labs

Loja full stack mínima para aulas **autorizadas** de cibersegurança. O projeto usa Spring Boot, Java 21, MySQL com Hibernate e uma interface estática em HTML/CSS/JS.

## Executar localmente

Com Java 21, Maven e MySQL instalados, inicie o serviço MySQL e execute `mvn spring-boot:run` na raiz. Configure as credenciais do banco e as chaves de aplicação por variáveis de ambiente; localmente, também é possível usar o arquivo ignorado `src/main/resources/application-local.properties`. O Hibernate cria e atualiza as tabelas a partir das entidades JPA (`ddl-auto=update`); não há inicialização por scripts SQL. Exemplo em Bash para configurar o banco, as chaves e criar um administrador:

```bash
export JWT_SECRET="<segredo Base64 de 32 bytes, gerado uma única vez>"
export PII_ENCRYPTION_KEY="<chave Base64 de 32 bytes, gerada uma única vez>"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="<senha local do MySQL>"
export ADMIN_BOOTSTRAP_ENABLED=true
export ADMIN_NAME="Administrador do Lab"
export ADMIN_EMAIL="admin@lab.local"
export ADMIN_PASSWORD="defina-uma-senha-forte"
mvn spring-boot:run
```

Abra `http://localhost:8080`. O registro público cria contas com role USER; o primeiro acesso administrativo só é provisionado se as variáveis de bootstrap forem definidas antes da inicialização.

Exemplo em PowerShell:

```powershell
$env:JWT_SECRET = '<segredo Base64 de 32 bytes, gerado uma única vez>'
$env:PII_ENCRYPTION_KEY = '<chave Base64 de 32 bytes, gerada uma única vez>'
$env:SPRING_DATASOURCE_USERNAME = 'root'
$env:SPRING_DATASOURCE_PASSWORD = '<senha local do MySQL>'
$env:ADMIN_BOOTSTRAP_ENABLED = 'true'
$env:ADMIN_NAME = 'Administrador do Lab'
$env:ADMIN_EMAIL = 'admin@lab.local'
$env:ADMIN_PASSWORD = '<defina uma senha forte>'
mvn spring-boot:run
```

Defina uma chave PII estável e guarde uma cópia segura: trocá-la impede descriptografar dados pessoais já persistidos. Para outro servidor MySQL, defina também `SPRING_DATASOURCE_URL`. Para recuperação de senha por e-mail, configure `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD` e `APP_MAIL_FROM`; por padrão, autenticação e STARTTLS ficam ligados e podem ser desativados com `SMTP_AUTH=false` e `SMTP_STARTTLS=false` quando o servidor local não os exigir. Sem SMTP, a aplicação inicia normalmente e o endpoint mantém a resposta genérica, mas não envia mensagem.

O scheduler de laboratório inicia automaticamente junto com a aplicação e registra no console o início, o sucesso ou a falha de cada ação. Na inicialização, cria duas contas fictícias; a cada 10 segundos, cadastra um produto e tenta criar pedidos; a cada 30 segundos, tenta criar uma conta e cadastrar um cartão; a cada 60 segundos, avança uma entrega pendente. Os dados são persistidos no MySQL local. As operações chamam os serviços internos e não geram tráfego HTTP interceptável.

O login e cadastro ficam em `http://localhost:8080/login.html`; a recuperação fica em `/password-reset.html`; administração aparece para contas com permissions. A configuração padrão usa MySQL e Hibernate com `ddl-auto=update`, então os dados permanecem depois que a aplicação para. H2 fica reservado aos testes.

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
