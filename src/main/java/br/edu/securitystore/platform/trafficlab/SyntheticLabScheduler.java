package br.edu.securitystore.platform.trafficlab;

import br.edu.securitystore.catalog.core.application.CatalogService;
import br.edu.securitystore.iam.identity.core.application.IdentityService;
import br.edu.securitystore.iam.identity.core.domain.Identity;
import br.edu.securitystore.logistics.api.module.DeliveryOperations;
import br.edu.securitystore.logistics.api.module.DeliveryQuery;
import br.edu.securitystore.logistics.api.module.DeliveryQuery.DeliveryStatus;
import br.edu.securitystore.sales.core.application.OrderService;
import br.edu.securitystore.sales.infra.persistence.SavedCardService;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Generates synthetic lab data and reports each scheduled action to the application log. */
@Component
public class SyntheticLabScheduler {
    private static final Logger log = LoggerFactory.getLogger(SyntheticLabScheduler.class);
    private static final String[] FIRST_NAMES = {"Alex", "Beatriz", "Caio", "Dani", "Eli"};
    private static final String[] LAST_NAMES = {"Teste", "Exemplo", "Laboratorio", "Demo", "Ficticio"};
    private final IdentityService identities;
    private final CatalogService catalog;
    private final OrderService orders;
    private final SavedCardService cards;
    private final DeliveryOperations deliveries;
    private final DeliveryQuery deliveryQuery;
    private final SecureRandom random = new SecureRandom();
    private final List<Long> userIds = new CopyOnWriteArrayList<>();
    private final ConcurrentLinkedDeque<Long> orderIds = new ConcurrentLinkedDeque<>();
    private volatile boolean initialized;
    private long productSequence;

    public SyntheticLabScheduler(IdentityService identities, CatalogService catalog, OrderService orders,
            SavedCardService cards, DeliveryOperations deliveries, DeliveryQuery deliveryQuery) {
        this.identities = identities;
        this.catalog = catalog;
        this.orders = orders;
        this.cards = cards;
        this.deliveries = deliveries;
        this.deliveryQuery = deliveryQuery;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        log.info("Traffic Lab: inicializando scheduler e contas sintéticas; os ciclos periódicos vão começar.");
        for (int i = 1; i <= 2; i++) {
            Identity identity = runValue("criar conta sintética inicial " + i, this::createSyntheticUser);
            if (identity != null) {
                userIds.add(identity.id());
            }
        }
        initialized = true;
        log.info("Traffic Lab: scheduler ativo; contas sintéticas prontas={}", userIds.size());
    }

    @Scheduled(fixedRate = 30_000, initialDelay = 30_000)
    public void createUser() {
        if (!canRun("criar conta sintética")) return;
        if (userIds.size() >= 1_000) {
            log.warn("Traffic Lab: ação ignorada; limite local de 1000 contas sintéticas atingido.");
            return;
        }
        Identity identity = runValue("criar conta sintética", this::createSyntheticUser);
        if (identity != null) {
            userIds.add(identity.id());
            log.info("Traffic Lab: total de contas sintéticas={}", userIds.size());
        }
    }

    @Scheduled(fixedRate = 10_000, initialDelay = 10_000)
    public void replenishAndPurchase() {
        if (!canRun("repor produto e criar pedidos")) return;
        String productName = "Item de laboratório " + (++productSequence);
        var product = runValue("cadastrar produto sintético", () ->
                catalog.create(productName, "Produto sintético do Traffic Lab", new BigDecimal("9.99"), 3));
        if (product == null) return;

        List<Long> buyers = randomUsers(2);
        if (buyers.isEmpty()) {
            log.warn("Traffic Lab: pedidos não criados; nenhuma conta sintética disponível.");
            return;
        }
        for (Long userId : buyers) {
            var order = runValue("criar pedido sintético para conta " + userId,
                    () -> orders.create(userId, product.getId(), 1));
            if (order != null) {
                orderIds.addLast(order.id());
                log.info("Traffic Lab: pedido sintético criado; pedido={}", order.id());
            }
        }
    }

    @Scheduled(fixedRate = 30_000, initialDelay = 30_000)
    public void registerCard() {
        if (!canRun("cadastrar cartão fictício")) return;
        if (userIds.isEmpty()) {
            log.warn("Traffic Lab: cartão não cadastrado; nenhuma conta sintética disponível.");
            return;
        }
        Long userId = userIds.get(random.nextInt(userIds.size()));
        runAction("cadastrar cartão fictício para conta " + userId,
                () -> cards.add(userId, "9999 0000 0000 " + random.nextInt(1000, 10_000)));
    }

    @Scheduled(fixedRate = 60_000, initialDelay = 60_000)
    public void advanceDelivery() {
        if (!canRun("avançar entrega")) return;
        int remaining = orderIds.size();
        while (remaining-- > 0) {
            Long orderId = orderIds.pollFirst();
            if (orderId == null) return;
            var summary = runValue("consultar entrega do pedido " + orderId,
                    () -> deliveryQuery.findByOrderId(orderId));
            if (summary == null) {
                orderIds.addLast(orderId);
                continue;
            }
            var current = summary.map(DeliveryQuery.DeliverySummary::status).orElse(null);
            if (current == null || current == DeliveryStatus.DELIVERED || current == DeliveryStatus.CANCELLED) {
                log.info("Traffic Lab: pedido {} sem entrega pendente; status={}", orderId, current);
                continue;
            }
            var delivery = runValue("avançar entrega do pedido " + orderId,
                    () -> deliveries.updateStatus(orderId, nextStatus(current)));
            if (delivery == null) {
                orderIds.addLast(orderId);
            } else {
                if (delivery.status() != DeliveryStatus.DELIVERED) orderIds.addLast(orderId);
                log.info("Traffic Lab: entrega atualizada; pedido={}, status={}", orderId, delivery.status());
            }
            return;
        }
        log.info("Traffic Lab: nenhuma entrega pendente para avançar.");
    }

    private Identity createSyntheticUser() {
        String email = "lab-" + UUID.randomUUID() + "@example.invalid";
        String name = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)] + " "
                + LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        Identity identity = identities.create(name, email, "Lab-" + UUID.randomUUID() + "-A9!");
        identities.updateProfile(identity.id(), fakeCpf(), "1199999" + random.nextInt(1000, 10_000),
                "Rua de Teste", "" + random.nextInt(1, 9999), "", "Bairro Sintético",
                "Cidade Demo", "SP", "01001000");
        return identity;
    }

    private String fakeCpf() {
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < 9; i++) value.append(random.nextInt(10));
        value.append(cpfDigit(value, 10));
        value.append(cpfDigit(value, 11));
        return value.toString();
    }

    private int cpfDigit(CharSequence digits, int weight) {
        int sum = 0;
        for (int i = 0; i < digits.length(); i++) sum += (digits.charAt(i) - '0') * (weight - i);
        int result = (sum * 10) % 11;
        return result == 10 ? 0 : result;
    }

    private List<Long> randomUsers(int count) {
        List<Long> candidates = new ArrayList<>(userIds);
        java.util.Collections.shuffle(candidates, random);
        return candidates.subList(0, Math.min(count, candidates.size()));
    }

    private DeliveryStatus nextStatus(DeliveryStatus current) {
        return switch (current) {
            case PREPARING -> DeliveryStatus.SHIPPED;
            case SHIPPED -> DeliveryStatus.IN_TRANSIT;
            case IN_TRANSIT, OUT_FOR_DELIVERY -> DeliveryStatus.DELIVERED;
            case DELIVERED, CANCELLED -> current;
        };
    }

    private boolean canRun(String action) {
        if (initialized) return true;
        log.warn("Traffic Lab: ação '{}' ignorada; inicialização ainda não terminou.", action);
        return false;
    }

    private <T> T runValue(String action, Supplier<T> operation) {
        log.info("Traffic Lab: iniciando ação: {}", action);
        try {
            T result = operation.get();
            log.info("Traffic Lab: ação concluída com sucesso: {}", action);
            return result;
        } catch (RuntimeException exception) {
            log.error("Traffic Lab: ação falhou: {}", action, exception);
            return null;
        }
    }

    private boolean runAction(String action, Runnable operation) {
        log.info("Traffic Lab: iniciando ação: {}", action);
        try {
            operation.run();
            log.info("Traffic Lab: ação concluída com sucesso: {}", action);
            return true;
        } catch (RuntimeException exception) {
            log.error("Traffic Lab: ação falhou: {}", action, exception);
            return false;
        }
    }
}
