# Documentação global

## Comece aqui

- [Instalação e execução](../README.md)
- [Regras do laboratório](lab-rules.md)
- [Backlog](backlog.md)
- [Revisão de segurança](security-review.md)
- [Estado das branches](repository-branches.md)

## Arquitetura

O projeto é um único aplicativo Spring Boot organizado em módulos por domínio. `api.http` expõe os endpoints, `api.module` define contratos entre módulos, `core` contém regras de negócio e `infra` integra persistência e outros módulos. Os módulos compartilham o mesmo MySQL, mas referenciam outros contextos por IDs e contratos, sem relações JPA entre módulos.

| Módulo | Responsabilidade | Documentação |
| --- | --- | --- |
| IAM | Identidades, autenticação e permissões | [IAM](../src/main/java/br/edu/securitystore/iam/docs/README.md) |
| Catálogo | Produtos e estoque | [Catálogo](../src/main/java/br/edu/securitystore/catalog/docs/README.md) |
| Vendas | Pedidos e cartões fictícios | [Vendas](../src/main/java/br/edu/securitystore/sales/docs/README.md) |
| Logística | Estados e previsão de entrega | [Logística](../src/main/java/br/edu/securitystore/logistics/docs/README.md) |
| Platform | Segurança HTTP, criptografia, erros e agendamento | [Platform](../src/main/java/br/edu/securitystore/platform/docs/README.md) |
| Config | Configurações gerais da aplicação | [Config](../src/main/java/br/edu/securitystore/config/docs/README.md) |

O IAM detalha [Identity](../src/main/java/br/edu/securitystore/iam/identity/docs/README.md), [Authentication](../src/main/java/br/edu/securitystore/iam/authentication/docs/README.md) e [Authorization](../src/main/java/br/edu/securitystore/iam/authorization/docs/README.md).
