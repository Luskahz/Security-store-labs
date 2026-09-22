# Catálogo

Responsável por produtos, preço e estoque. `CatalogService` atende o HTTP; `CatalogOperations` é o contrato interno consumido por vendas para reservar estoque e obter um retrato do produto. A reserva usa lock pessimista dentro da transação de criação do pedido.

Fluxos HTTP: `GET /api/products`, `POST /api/products` e `DELETE /api/products/{id}`. O modelo de domínio contém a regra de estoque; `ProductEntity` e Spring Data ficam em `infra.persistence`. A exclusão de produto não apaga pedidos, pois vendas guarda um retrato próprio da compra.
