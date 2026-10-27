# HTTPS com Nginx no Linux/Kali

Este guia publica o Security Store Labs na rede do laboratório com Nginx terminando TLS e encaminhando as requisições para o Spring Boot local:

```text
navegador -- HTTPS :443 --> Nginx -- HTTP local --> Spring Boot :8080
```

O exemplo usa o IP atual `10.81.211.139`; substitua-o pelo IP autorizado e atual da interface em Bridge. Não use um IP estático sem autorização/reserva da rede. O banco MySQL não faz parte do proxy e não deve ser publicado na rede.

## 1. Instalar o Nginx e preparar o certificado

No Kali, instale o Nginx e o OpenSSL:

```bash
sudo apt update
sudo apt install nginx openssl
```

Para um laboratório sem uma CA confiável disponível, crie um certificado autoassinado cujo SAN contenha o IP da VM. Edite o IP abaixo antes de executar:

```bash
APP_VM_IP=10.81.211.139
sudo openssl req -x509 -newkey rsa:3072 -sha256 -days 365 -nodes \
  -keyout /etc/ssl/private/security-store-labs.key \
  -out /etc/ssl/certs/security-store-labs.crt \
  -subj "/CN=$APP_VM_IP" \
  -addext "subjectAltName=IP:$APP_VM_IP"
sudo chmod 600 /etc/ssl/private/security-store-labs.key
```

O certificado autoassinado cifra a conexão, mas o navegador exibirá um aviso até que o certificado seja confiado no dispositivo cliente. Para acesso sem aviso, use um certificado emitido por uma CA confiável para os clientes do laboratório ou distribua/confie o certificado apenas nos dispositivos autorizados. Não desative a validação TLS globalmente. Mantenha a chave privada fora do repositório.

## 2. Configurar o proxy Nginx

Copie `deploy/nginx/security-store-labs.conf` para `/etc/nginx/sites-available/security-store-labs`. Se o IP da VM mudou, atualize o `server_name` nos dois blocos. Ative o site e valide a configuração antes de recarregar:

```bash
sudo cp deploy/nginx/security-store-labs.conf /etc/nginx/sites-available/security-store-labs
sudo ln -s /etc/nginx/sites-available/security-store-labs /etc/nginx/sites-enabled/security-store-labs
sudo nginx -t
sudo systemctl reload nginx
```

O site padrão do pacote Nginx pode continuar habilitado: a configuração acima corresponde ao IP explícito da VM. Se o link simbólico já existir, não o crie novamente.

## 3. Iniciar o app apenas no loopback

No `start.local.sh` ignorado pelo Git, defina as variáveis abaixo, usando o IP corrente da VM. Isso restringe o Spring Boot à própria VM, habilita o processamento dos cabeçalhos encaminhados pelo Nginx e faz os e-mails de recuperação usarem HTTPS:

```bash
export SERVER_ADDRESS='127.0.0.1'
export SERVER_FORWARD_HEADERS_STRATEGY='framework'
export APP_PASSWORD_RESET_URL='https://10.81.211.139/password-reset.html'
```

Reinicie o app com `bash start.sh` depois de salvar `start.local.sh`. O Nginx então recebe os acessos HTTP/HTTPS; clientes da rede não acessam diretamente a porta `8080`.

## 4. Liberar somente HTTP e HTTPS no UFW

Como o UFW está ativo no exemplo, permita as portas web pela interface da rede do laboratório. Ajuste a sub-rede se a rede autorizada for diferente:

```bash
sudo ufw allow in on eth0 from 10.81.211.0/24 to any port 80 proto tcp
sudo ufw allow in on eth0 from 10.81.211.0/24 to any port 443 proto tcp
sudo ufw status numbered
```

Depois de confirmar HTTPS pelo Nginx, remova pelo número exibido em `ufw status numbered` a regra anterior que expunha `8080/tcp`. Não remova outras regras por engano. Se você administra a VM por SSH, permita primeiro o SSH pelo caminho de administração usado antes de ativar ou alterar o firewall.

## 5. Acessar

Abra `https://10.81.211.139/` ou `https://10.81.211.139/login.html`. O HTTP na porta 80 redireciona para HTTPS. Como o certificado de exemplo é autoassinado, clientes precisam confiar nele para remover o aviso do navegador.

Se o IP mudar, atualize o certificado/SAN, `server_name`, `APP_PASSWORD_RESET_URL` e as regras de firewall para o IP/sub-rede autorizados.

