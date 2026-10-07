# Security Store Labs

Loja educacional para laboratórios autorizados de cibersegurança. O backend é um monólito modular em Java 21 e Spring Boot 3.5; a interface usa HTML, CSS e JavaScript servidos pela própria aplicação. O banco padrão é MySQL.

## Instalação

1. Instale **JDK 21**. No Windows, instale MySQL Server 8 como serviço do Windows. No Linux/Kali, instale Docker e confirme que `docker info` funciona. Não é necessário instalar Maven: o Maven Wrapper baixa a versão fixada no primeiro início.
2. Clone o repositório e entre na pasta raiz. No Windows, `start.bat` inicia o serviço MySQL local se estiver parado e inicia o Spring Boot. No Linux, `start.sh` cria ou inicia o contêiner `security-store-mysql` na porta 3306. O primeiro início baixa o Maven, as dependências Maven e, no Linux, a imagem MySQL.
3. Gere duas chaves aleatórias Base64 de **32 bytes**, configure-as como `JWT_SECRET` e `PII_ENCRYPTION_KEY`, e mantenha a segunda chave para conseguir ler os dados pessoais já gravados. No PowerShell, gere cada chave com `[Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))`; no Linux, use `openssl rand -base64 32`.
4. Configure as variáveis no ambiente ou em um arquivo local ignorado pelo Git, conforme o sistema abaixo. Não publique senhas ou chaves no repositório.

### Windows, sem IDE

Copie `start.example.bat` para `start.local.bat` na raiz e substitua os valores de exemplo pelas configurações locais. O arquivo de exemplo lista as variáveis aceitas pelo script e pela aplicação; `start.local.bat` é ignorado pelo Git.

Execute `start.bat` no Prompt de Comando ou `./start.bat` no PowerShell. O padrão é o serviço `MySQL80`; se o serviço tiver outro nome, defina `MYSQL_SERVICE_NAME` no `start.local.bat`. Se `application-local.properties` existir e as chaves não estiverem no ambiente, o script ativa o perfil `local` para carregar essa configuração ignorada pelo Git.

### Linux, inclusive Kali

Copie `start.example.sh` para `start.local.sh` e substitua os valores de exemplo. Esse arquivo é carregado pelo `start.sh` e ignorado pelo Git.

Execute `bash start.sh`. O script encontra a raiz pela própria localização; não depende de `/home/kali` nem exige IDE. Se o usuário não tiver acesso ao Docker, o script usa `sudo docker`.

Abra [http://localhost:8080](http://localhost:8080). O login fica em `/login.html`. No Windows, a janela do `start.bat` permanece aberta enquanto o Spring Boot está em execução; use `Ctrl+C` ou feche a janela para encerrar a aplicação. Se o script tiver iniciado o serviço MySQL, ele o para ao encerrar normalmente; se o serviço já estava rodando, deixa-o ativo. No Linux, o volume Docker conserva os dados do banco.

## Configuração

As propriedades padrão usam `root` / `aluno` e `localhost:3306/security_store` com `createDatabaseIfNotExist=true`. No Windows, configure em `start.local.bat` as credenciais que você definiu no MySQL local. No Linux, `start.sh` cria o contêiner com a senha `SPRING_DATASOURCE_PASSWORD` (padrão `aluno`). Se o contêiner já existir, mudar a variável não altera a senha que o MySQL já guarda. Para outro banco ou porta, configure `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`; no Windows ajuste também `MYSQL_PORT` se necessário.

`ADMIN_BOOTSTRAP_ENABLED` é `false` por padrão. Quando ativado, exige nome, e-mail e senha com pelo menos oito caracteres. O administrador é provisionado no início e recebe a role ADMIN. O cadastro público cria usuários comuns. Evite usar as credenciais de exemplo fora de um laboratório local.

`SPRING_JPA_HIBERNATE_DDL_AUTO` usa `update` por padrão; o Hibernate cria e atualiza tabelas, sem scripts SQL de inicialização. `SPRING_JPA_FORMAT_SQL` controla a formatação do SQL. `application-mysql.properties` pode ser selecionado com `SPRING_PROFILES_ACTIVE=mysql`. O arquivo local `application-local.properties` é ignorado pelo Git; o `start.bat` ativa o perfil `local` automaticamente quando precisa obter as chaves desse arquivo.

SMTP é opcional. Para envio de recuperação de senha, configure `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD` e `APP_MAIL_FROM`; `SMTP_AUTH` e `SMTP_STARTTLS` controlam autenticação e STARTTLS. Sem SMTP, a aplicação inicia, mas não envia e-mail. `APP_PASSWORD_RESET_URL`, `PASSWORD_RECOVERY_TTL` e `PASSWORD_RECOVERY_COOLDOWN` ajustam o fluxo de recuperação.

## Funcionalidades e documentação

- `POST /auth/login`, `POST /auth/refresh`, `GET /auth/me` — autenticação JWT.
- `GET /api/products` — catálogo; escrita de produtos exige permissão.
- `GET/POST /api/orders` e `POST /api/orders/{id}/pay` — pedidos e pagamento fictício.
- `GET/PATCH /api/deliveries/orders/{orderId}` — entrega.
- `/admin/` — páginas administrativas para contas autorizadas.

O índice em [docs/README.md](docs/README.md) reúne instalação, arquitetura, regras do laboratório e documentação de cada módulo. O scheduler de laboratório registra ações sintéticas no console e usa os serviços internos; ele não produz tráfego HTTP próprio.
