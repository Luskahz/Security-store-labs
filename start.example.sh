# Copie este arquivo como start.local.sh e substitua os valores de exemplo.
# Gere chaves Base64 aleatorias com: openssl rand -base64 32
# Nunca use estes placeholders como chaves reais.
export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/security_store?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America%2FSao_Paulo'
export SPRING_DATASOURCE_USERNAME='root'
export SPRING_DATASOURCE_PASSWORD='aluno' # troque pela senha configurada no MySQL local/contêiner
export JWT_SECRET='SUBSTITUA_POR_CHAVE_BASE64_ALEATORIA_DE_32_BYTES'
export PII_ENCRYPTION_KEY='SUBSTITUA_POR_OUTRA_CHAVE_BASE64_ALEATORIA_DE_32_BYTES'
export ADMIN_BOOTSTRAP_ENABLED='true'
export ADMIN_NAME='Administrador do Lab'
export ADMIN_EMAIL='admin@lab.local'
export ADMIN_PASSWORD='SUBSTITUA_POR_UMA_SENHA_FORTE'

# Usado pelo start.sh no Linux para inicializar o MySQL via Docker.
export USE_DOCKER_MYSQL='true'
export MYSQL_CONTAINER='security-store-mysql'

# Opcional: configure SMTP para envio de recuperação de senha.
export SMTP_HOST=''
export SMTP_PORT='0'
export SMTP_USERNAME=''
export SMTP_PASSWORD=''
export APP_MAIL_FROM='no-reply@localhost'
