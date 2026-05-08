package com.shoppoc.order.notification.application;

import com.shoppoc.order.notification.domain.NotificationRepository;
import com.shoppoc.order.notification.domain.RecordedNotification;
import org.springframework.stereotype.Service;

@Service
public class NotificationRecordingService implements NotificationRecorder {

    private final NotificationRepository notificationRepository;

    public NotificationRecordingService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void recordPaymentAuthorized(String customerEmail, String orderId, String paymentId) {
        notificationRepository.save(RecordedNotification.paymentAuthorized(customerEmail, orderId, paymentId));
    }

    @Override
    public void recordPaymentRejected(String customerEmail, String orderId, String paymentId, String reason) {
        notificationRepository.save(RecordedNotification.paymentRejected(customerEmail, orderId, paymentId, reason));
    }
}
