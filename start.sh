#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

if [[ -f start.local.sh ]]; then
  # Configuração local, ignorada pelo Git.
  source "$PROJECT_DIR/start.local.sh"
fi

: "${SPRING_DATASOURCE_USERNAME:=root}"
: "${SPRING_DATASOURCE_PASSWORD:=aluno}"
: "${USE_DOCKER_MYSQL:=true}"
: "${MYSQL_CONTAINER:=security-store-mysql}"
export SPRING_DATASOURCE_USERNAME SPRING_DATASOURCE_PASSWORD
export MAVEN_USER_HOME="${MAVEN_USER_HOME:-$PROJECT_DIR/.m2-cache}"

for command_name in java mvn; do
  if ! command -v "$command_name" >/dev/null 2>&1; then
    echo "Erro: instale $command_name e adicione-o ao PATH." >&2
    exit 1
  fi
done

if [[ -z "${JWT_SECRET:-}" || -z "${PII_ENCRYPTION_KEY:-}" ]]; then
  echo "Erro: configure JWT_SECRET e PII_ENCRYPTION_KEY em start.local.sh ou no ambiente." >&2
  exit 1
fi

if [[ "$USE_DOCKER_MYSQL" == "true" ]]; then
  if ! command -v docker >/dev/null 2>&1; then
    echo "Erro: Docker não encontrado. Instale-o ou use USE_DOCKER_MYSQL=false com MySQL local." >&2
    exit 1
  fi
  DOCKER=(docker)
  if ! docker info >/dev/null 2>&1; then
    DOCKER=(sudo docker)
    "${DOCKER[@]}" info >/dev/null
  fi

  if ! "${DOCKER[@]}" container inspect "$MYSQL_CONTAINER" >/dev/null 2>&1; then
    echo "Criando contêiner MySQL $MYSQL_CONTAINER..."
    "${DOCKER[@]}" run -d --name "$MYSQL_CONTAINER" \
      -e "MYSQL_ROOT_PASSWORD=$SPRING_DATASOURCE_PASSWORD" \
      -p 3306:3306 -v security-store-mysql-data:/var/lib/mysql mysql:8.4 >/dev/null
  elif [[ "$("${DOCKER[@]}" inspect -f '{{.State.Running}}' "$MYSQL_CONTAINER")" != "true" ]]; then
    "${DOCKER[@]}" start "$MYSQL_CONTAINER" >/dev/null
  fi

  echo "Aguardando MySQL..."
  for ((attempt=0; attempt<60; attempt++)); do
    if "${DOCKER[@]}" exec -e "MYSQL_PWD=$SPRING_DATASOURCE_PASSWORD" "$MYSQL_CONTAINER" \
      mysql -uroot -e 'SELECT 1' >/dev/null 2>&1; then
      break
    fi
    if (( attempt == 59 )); then
      echo "Erro: MySQL não respondeu em 60 segundos." >&2
      exit 1
    fi
    sleep 1
  done
fi

echo "Iniciando Security Store Labs em http://localhost:8080"
exec bash "$PROJECT_DIR/mvnw" spring-boot:run
