package com.shoppoc.order.notification.application;

public interface NotificationRecorder {

    void recordPaymentAuthorized(String customerEmail, String orderId, String paymentId);

    void recordPaymentRejected(String customerEmail, String orderId, String paymentId, String reason);
}
