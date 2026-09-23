# Vendas e entrega

Responsável por pedidos, pagamento fictício e status de entrega. `OrderService` coordena os fluxos e a transação de compra. O pedido guarda IDs e um retrato de cliente/produto, sem relações JPA com outros contextos. `CustomerLookup` e `ProductReservation` são ports implementados por adapters que chamam as APIs internas de IAM e catálogo.

Fluxos HTTP: `GET/POST /api/orders`, `POST /api/orders/{id}/pay` e `PATCH /api/orders/{id}/delivery`. O pagamento apenas altera o estado de um pedido mockado; não há gateway financeiro ou dados de cartão. O cliente só pode pagar seus próprios pedidos. ADMIN atualiza a entrega. DTOs HTTP são separados do domínio e da persistência.
