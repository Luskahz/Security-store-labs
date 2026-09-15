# Security Store Labs

Loja full stack mínima para aulas **autorizadas** de cibersegurança. O projeto usa Spring Boot, Java 21, H2 e uma interface estática em HTML/CSS/JS.

## Executar

```bash
mvn spring-boot:run
```

Acesse `http://localhost:8080`. Contas fictícias iniciais:

- `admin@lab.local` / `Admin123!` (ADMIN)
- `aluno@lab.local` / `Aluno123!` (CUSTOMER)

O console H2 fica desabilitado. Todos os dados são mockados e recriados a cada execução.

## API inicial

- `POST /api/iam/register` — cadastro
- `GET /api/iam/me` — identidade autenticada
- `GET /api/products` — catálogo público
- `POST/DELETE /api/products/**` — administração de produtos
- `POST /api/orders` e `GET /api/orders` — compras do usuário
- `POST /api/orders/{id}/pay` — pagamento fictício
- `PATCH /api/orders/{id}/delivery` — atualização de entrega por ADMIN

Veja o planejamento em [`docs/backlog.md`](docs/backlog.md) e as regras do laboratório em [`docs/lab-rules.md`](docs/lab-rules.md).
