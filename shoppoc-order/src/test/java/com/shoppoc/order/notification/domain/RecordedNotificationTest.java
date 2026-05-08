package com.shoppoc.order.notification.domain;

import com.shoppoc.shared.error.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordedNotificationTest {

    @Test
    void paymentAuthorizedNotificationHasExpectedFields() {
        RecordedNotification notification = RecordedNotification.paymentAuthorized("user@example.com", "order-1", "pay-1");

        assertEquals(NotificationType.ORDER_PAYMENT_AUTHORIZED, notification.getType());
        assertEquals(NotificationStatus.RECORDED, notification.getStatus());
        assertEquals("Order payment authorized", notification.getSubject());
        assertTrue(notification.getBody().contains("order-1"));
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    void paymentRejectedNotificationHasReasonInBody() {
        RecordedNotification notification = RecordedNotification.paymentRejected("user@example.com", "order-2", "pay-2", "Declined");

        assertEquals(NotificationType.ORDER_PAYMENT_REJECTED, notification.getType());
        assertEquals(NotificationStatus.RECORDED, notification.getStatus());
        assertEquals("Order payment rejected", notification.getSubject());
        assertTrue(notification.getBody().contains("Declined"));
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    void blankCustomerRejected() {
        assertThrows(BusinessException.class, () -> RecordedNotification.paymentAuthorized(" ", "order-1", "pay-1"));
    }

    @Test
    void blankOrderRejected() {
        assertThrows(BusinessException.class, () -> RecordedNotification.paymentRejected("user@example.com", " ", "pay-1", "reason"));
    }
}
