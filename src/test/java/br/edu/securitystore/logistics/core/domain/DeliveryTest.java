package br.edu.securitystore.logistics.core.domain;

import static org.junit.jupiter.api.Assertions.*;
import br.edu.securitystore.logistics.api.module.DeliveryQuery.DeliveryStatus;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class DeliveryTest {
    @Test void followsDeliveryFlowAndRecordsMilestones() {
        Delivery delivery = new Delivery(1L, "LAB-00000001", Instant.now().plusSeconds(86400));
        delivery.updateStatus(DeliveryStatus.SHIPPED);
        assertNotNull(delivery.getShippedAt());
        delivery.updateStatus(DeliveryStatus.IN_TRANSIT);
        delivery.updateStatus(DeliveryStatus.OUT_FOR_DELIVERY);
        delivery.updateStatus(DeliveryStatus.DELIVERED);
        assertNotNull(delivery.getDeliveredAt());
        assertEquals(DeliveryStatus.DELIVERED, delivery.getStatus());
    }

    @Test void rejectsInvalidTransitionsAndTerminalReopen() {
        Delivery delivery = new Delivery(1L, "LAB-00000001", Instant.now().plusSeconds(86400));
        assertThrows(IllegalStateException.class, () -> delivery.updateStatus(DeliveryStatus.DELIVERED));
        delivery.updateStatus(DeliveryStatus.CANCELLED);
        assertThrows(IllegalStateException.class, () -> delivery.updateStatus(DeliveryStatus.SHIPPED));
    }
}
