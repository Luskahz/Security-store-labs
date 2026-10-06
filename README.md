# Security Store Labs

Loja educacional para laboratórios autorizados de cibersegurança. O backend é um monólito modular em Java 21 e Spring Boot 3.5; a interface usa HTML, CSS e JavaScript servidos pela própria aplicação. O banco padrão é MySQL.

## Instalação

1. Instale **JDK 21**, **Maven 3.9+** e **Docker**. Verifique com `java -version`, `mvn -version` e `docker info`. Sem Docker, instale e inicie MySQL 8 e configure `USE_DOCKER_MYSQL=false`.
2. Clone o repositório e entre na pasta raiz. Os scripts `start.bat` e `start.sh` criam ou iniciam o contêiner `security-store-mysql` na porta 3306, aguardam o banco e iniciam o Spring Boot. O primeiro início baixa as dependências Maven e a imagem MySQL.
3. Gere duas chaves aleatórias Base64 de **32 bytes**, configure-as como `JWT_SECRET` e `PII_ENCRYPTION_KEY`, e mantenha a segunda chave para conseguir ler os dados pessoais já gravados. No PowerShell, gere cada chave com `[Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))`; no Linux, use `openssl rand -base64 32`.
4. Configure as variáveis no ambiente ou em um arquivo local ignorado pelo Git, conforme o sistema abaixo. Não publique senhas ou chaves no repositório.

### Windows, sem IDE

Crie `start.local.bat` na raiz com seus valores (substitua os campos entre `<...>`):

```bat
@echo off
set "JWT_SECRET=<chave Base64>"
set "PII_ENCRYPTION_KEY=<outra chave Base64>"
set "SPRING_DATASOURCE_PASSWORD=aluno"
set "ADMIN_BOOTSTRAP_ENABLED=true"
set "ADMIN_NAME=Administrador do Lab"
set "ADMIN_EMAIL=admin@lab.local"
set "ADMIN_PASSWORD=<senha de pelo menos 8 caracteres>"
```

Execute `start.bat` no Prompt de Comando ou `./start.bat` no PowerShell. O arquivo local é opcional se as variáveis já estiverem definidas no ambiente.

### Linux, inclusive Kali

Crie `start.local.sh` na raiz:

```bash
export JWT_SECRET='<chave Base64>'
export PII_ENCRYPTION_KEY='<outra chave Base64>'
export SPRING_DATASOURCE_PASSWORD='aluno'
export ADMIN_BOOTSTRAP_ENABLED=true
export ADMIN_NAME='Administrador do Lab'
export ADMIN_EMAIL='admin@lab.local'
export ADMIN_PASSWORD='<senha de pelo menos 8 caracteres>'
```

Execute `bash start.sh`. O script encontra a raiz pela própria localização; não depende de `/home/kali` nem exige IDE. Se o usuário não tiver acesso ao Docker, o script usa `sudo docker`.

Abra [http://localhost:8080](http://localhost:8080). O login fica em `/login.html`. Encerre com `Ctrl+C`; o volume Docker conserva os dados do banco.

## Configuração

Os scripts usam `root` / `aluno` para o MySQL local criado por eles. Em outro banco, configure `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` e `USE_DOCKER_MYSQL=false`. A URL padrão usa `localhost:3306/security_store` e `createDatabaseIfNotExist=true`. Para conectar como outro usuário, crie o banco e conceda as permissões necessárias. Se o contêiner já existir, sua senha original continua valendo; mudar a variável não altera a senha armazenada no MySQL.

`ADMIN_BOOTSTRAP_ENABLED` é `false` por padrão. Quando ativado, exige nome, e-mail e senha com pelo menos oito caracteres. O administrador é provisionado no início e recebe a role ADMIN. O cadastro público cria usuários comuns. Evite usar as credenciais de exemplo fora de um laboratório local.

`SPRING_JPA_HIBERNATE_DDL_AUTO` usa `update` por padrão; o Hibernate cria e atualiza tabelas, sem scripts SQL de inicialização. `SPRING_JPA_FORMAT_SQL` controla a formatação do SQL. `application-mysql.properties` pode ser selecionado com `SPRING_PROFILES_ACTIVE=mysql`. O antigo `application-local.properties` ignorado pelo Git só é carregado se o perfil `local` for ativado explicitamente.

SMTP é opcional. Para envio de recuperação de senha, configure `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD` e `APP_MAIL_FROM`; `SMTP_AUTH` e `SMTP_STARTTLS` controlam autenticação e STARTTLS. Sem SMTP, a aplicação inicia, mas não envia e-mail. `APP_PASSWORD_RESET_URL`, `PASSWORD_RECOVERY_TTL` e `PASSWORD_RECOVERY_COOLDOWN` ajustam o fluxo de recuperação.

## Funcionalidades e documentação

- `POST /auth/login`, `POST /auth/refresh`, `GET /auth/me` — autenticação JWT.
- `GET /api/products` — catálogo; escrita de produtos exige permissão.
- `GET/POST /api/orders` e `POST /api/orders/{id}/pay` — pedidos e pagamento fictício.
- `GET/PATCH /api/deliveries/orders/{orderId}` — entrega.
- `/admin/` — páginas administrativas para contas autorizadas.

O índice em [docs/README.md](docs/README.md) reúne instalação, arquitetura, regras do laboratório e documentação de cada módulo. O scheduler de laboratório registra ações sintéticas no console e usa os serviços internos; ele não produz tráfego HTTP próprio.
