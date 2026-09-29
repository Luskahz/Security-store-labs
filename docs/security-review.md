# Revisão de segurança (OWASP)

Revisão manual do código e das configurações deste laboratório. Não substitui teste de invasão, threat modeling, análise de dependências nem avaliação de conformidade.

## Aplicado neste trabalho

- Perfil autenticado para CPF, telefone e endereço; validação no servidor, incluindo dígitos verificadores de CPF, formato de telefone e CEP. CPF, telefone e endereço ficam criptografados com AES-GCM; CPF também tem fingerprint HMAC para detectar duplicidade sem consulta ao texto puro.
- O endereço e telefone são copiados para o pedido na compra, preservando o destino histórico. CPF não é copiado para pedido nem mostrado à equipe de entrega.
- Recuperação de senha por e-mail com resposta uniforme, envio assíncrono após commit, token aleatório de 256 bits enviado no fragmento da URL, armazenamento somente do SHA-256, validade configurável (15 minutos por padrão), uso único, cooldown por conta e limite temporário por IP. Ao redefinir, a senha é re-hashada e as sessões são revogadas.
- BCrypt com custo 12 para senhas novas/redefinidas; chave JWT e chave de criptografia PII passaram a ser obrigatórias via ambiente. Bootstrap administrativo começa desligado e credenciais do banco não têm senha embutida no arquivo.
- CSP, `Referrer-Policy: no-referrer` e `Permissions-Policy`; o token de recuperação é removido da barra de endereço assim que a página o lê.

## Pendências antes de qualquer uso público

- O limite por IP da recuperação é local à memória de uma única instância. Para várias instâncias, usar rate limiter compartilhado; adicionar limite também a login/cadastro, monitoramento e controles anti-automação.
- O perfil armazena e-mail/nome e os dados de entrega de pedido conforme a operação exige. Definir prazo de retenção e rotina de exclusão; manter as chaves fora do banco, com backup e plano de rotação. A perda/rotação da `PII_ENCRYPTION_KEY` sem recriptografia torna os dados ilegíveis.
- O cliente mantém JWT/refresh token em `sessionStorage`; XSS pode capturá-los. Uma implantação pública deve preferir desenho de sessão com cookie `HttpOnly`, `Secure`, `SameSite` e proteção CSRF compatível.
- Configurar SMTP autenticado com TLS e URL HTTPS pública. A recuperação não envia e-mail se SMTP estiver indisponível; o endpoint preserva a resposta genérica. Monitorar o aviso de falha SMTP sem registrar endereço, token ou senha.
- Usar conta MySQL de privilégio mínimo em vez de `root`, TLS conforme ambiente, backups protegidos e segredos em gerenciador apropriado. CPF/endereço são dados pessoais; avaliar obrigações legais e consentimento/necessidade antes de coletá-los.
- Não há MFA nem limitação persistente de tentativas de login. Avaliar MFA e defesa contra credential stuffing antes de expor o sistema.

## Fontes

- [OWASP Forgot Password Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Forgot_Password_Cheat_Sheet.html)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)
- [OWASP Input Validation Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Input_Validation_Cheat_Sheet.html)
