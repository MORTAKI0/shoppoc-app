package com.shoppoc.order.notification.infrastructure.persistence;

import com.shoppoc.order.OrderTestApplication;
import com.shoppoc.order.notification.domain.NotificationType;
import com.shoppoc.order.notification.domain.RecordedNotification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ContextConfiguration(classes = OrderTestApplication.class)
@Import(JpaNotificationRepositoryAdapter.class)
class JpaNotificationRepositoryAdapterTest {

    @Autowired
    private JpaNotificationRepositoryAdapter repository;

    @Test
    void saveAuthorizedNotification() {
        RecordedNotification saved = repository.save(RecordedNotification.paymentAuthorized("user-a@example.com", "order-1", "pay-1"));

        assertNotNull(saved.getId());
        assertEquals(NotificationType.ORDER_PAYMENT_AUTHORIZED, saved.getType());
        assertEquals("Order payment authorized", saved.getSubject());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void saveRejectedNotification() {
        RecordedNotification saved = repository.save(RecordedNotification.paymentRejected("user-b@example.com", "order-2", "pay-2", "Declined"));

        assertEquals(NotificationType.ORDER_PAYMENT_REJECTED, saved.getType());
        assertTrue(saved.getBody().contains("Declined"));
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void findByCustomerEmailReturnsMatchingOnly() {
        repository.save(RecordedNotification.paymentAuthorized("user-a@example.com", "order-a1", "pay-a1"));
        repository.save(RecordedNotification.paymentRejected("user-a@example.com", "order-a2", "pay-a2", "Declined"));
        repository.save(RecordedNotification.paymentAuthorized("user-b@example.com", "order-b1", "pay-b1"));

        List<RecordedNotification> userA = repository.findByCustomerEmail("user-a@example.com");
        List<RecordedNotification> userB = repository.findByCustomerEmail("user-b@example.com");

        assertEquals(2, userA.size());
        assertEquals(1, userB.size());
        assertFalse(userA.get(0).getCustomerEmail().equals("user-b@example.com"));
    }

    @Test
    void findByOrderIdReturnsMatchingOnlyWithFieldsPreserved() {
        repository.save(RecordedNotification.paymentAuthorized("user-a@example.com", "order-x", "pay-x"));
        repository.save(RecordedNotification.paymentRejected("user-a@example.com", "order-y", "pay-y", "Declined"));

        List<RecordedNotification> orderX = repository.findByOrderId("order-x");
        List<RecordedNotification> orderY = repository.findByOrderId("order-y");

        assertEquals(1, orderX.size());
        assertEquals(NotificationType.ORDER_PAYMENT_AUTHORIZED, orderX.get(0).getType());
        assertNotNull(orderX.get(0).getCreatedAt());
        assertEquals(1, orderY.size());
        assertEquals(NotificationType.ORDER_PAYMENT_REJECTED, orderY.get(0).getType());
        assertTrue(orderY.get(0).getBody().contains("Declined"));
    }
}
