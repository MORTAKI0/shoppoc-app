package com.shoppoc.order.notification.application;

import com.shoppoc.order.notification.domain.NotificationRepository;
import com.shoppoc.order.notification.domain.NotificationType;
import com.shoppoc.order.notification.domain.RecordedNotification;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationRecordingServiceTest {

    private final NotificationRepository notificationRepository = mock(NotificationRepository.class);
    private final NotificationRecordingService service = new NotificationRecordingService(notificationRepository);

    @Test
    void recordsAuthorizedNotification() {
        when(notificationRepository.save(any(RecordedNotification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.recordPaymentAuthorized("user@example.com", "order-1", "pay-1");

        ArgumentCaptor<RecordedNotification> captor = ArgumentCaptor.forClass(RecordedNotification.class);
        verify(notificationRepository, times(1)).save(captor.capture());
        RecordedNotification saved = captor.getValue();
        assertEquals(NotificationType.ORDER_PAYMENT_AUTHORIZED, saved.getType());
        assertEquals("user@example.com", saved.getCustomerEmail());
        assertEquals("order-1", saved.getOrderId());
        assertEquals("pay-1", saved.getPaymentId());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void recordsRejectedNotification() {
        when(notificationRepository.save(any(RecordedNotification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.recordPaymentRejected("user@example.com", "order-2", "pay-2", "Declined");

        ArgumentCaptor<RecordedNotification> captor = ArgumentCaptor.forClass(RecordedNotification.class);
        verify(notificationRepository, times(1)).save(captor.capture());
        RecordedNotification saved = captor.getValue();
        assertEquals(NotificationType.ORDER_PAYMENT_REJECTED, saved.getType());
        assertTrue(saved.getBody().contains("Declined"));
        assertNotNull(saved.getCreatedAt());
    }
}
