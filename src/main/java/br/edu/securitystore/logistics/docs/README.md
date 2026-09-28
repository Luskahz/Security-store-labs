# Logistics

O módulo de logística gerencia a entrega vinculada ao pedido por `orderId`: status, código fictício de rastreio, previsão simples e datas de expedição e entrega. Ele expõe contratos em `api.module`, regras de transição no domínio e persistência própria. A previsão atual é demonstrativa (cinco dias corridos); não representa cálculo de transportadora.

## API

- `GET /api/deliveries/orders/{orderId}` — consulta entrega com `LOGISTICS_DELIVERY_READ` ou `SALES_ORDER_READ_ALL`.
- `PATCH /api/deliveries/orders/{orderId}` — altera status com `LOGISTICS_DELIVERY_UPDATE`.

Transições: `PREPARING → SHIPPED → IN_TRANSIT → OUT_FOR_DELIVERY → DELIVERED`; entrega pode avançar etapas e ser cancelada enquanto está em preparação. Estados finais não podem ser reabertos.
