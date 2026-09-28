# Vendas e entrega

Responsável por pedidos, pagamento fictício e status de entrega. `OrderService` coordena os fluxos e a transação de compra. O pedido guarda IDs e um retrato de cliente/produto, sem relações JPA com outros contextos. `CustomerLookup` e `ProductReservation` são ports implementados por adapters que chamam as APIs internas de IAM e catálogo.

Fluxos HTTP: `GET/POST /api/orders` e `POST /api/orders/{id}/pay`. A resposta do pedido inclui um resumo consultado do módulo `logistics`; o status, o rastreio e a previsão não são mantidos no agregado de vendas. O pagamento apenas altera o estado de um pedido mockado; não há gateway financeiro ou dados de cartão. O cliente só pode pagar seus próprios pedidos. DTOs HTTP são separados do domínio e da persistência.
