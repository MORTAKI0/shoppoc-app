package com.shoppoc.order.notification.domain;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;

import java.time.Instant;

public final class RecordedNotification {

    private final NotificationId id;
    private final String customerEmail;
    private final String orderId;
    private final String paymentId;
    private final NotificationType type;
    private final String subject;
    private final String body;
    private final NotificationStatus status;
    private final Instant createdAt;

    private RecordedNotification(NotificationId id,
                                 String customerEmail,
                                 String orderId,
                                 String paymentId,
                                 NotificationType type,
                                 String subject,
                                 String body,
                                 NotificationStatus status,
                                 Instant createdAt) {
        this.id = requireId(id);
        this.customerEmail = requireNotBlank(customerEmail, "Customer email must not be blank");
        this.orderId = requireNotBlank(orderId, "Order id must not be blank");
        this.paymentId = normalizeOptional(paymentId);
        this.type = requireType(type);
        this.subject = requireNotBlank(subject, "Notification subject must not be blank");
        this.body = requireNotBlank(body, "Notification body must not be blank");
        this.status = status == null ? NotificationStatus.RECORDED : status;
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
    }

    public static RecordedNotification paymentAuthorized(String customerEmail, String orderId, String paymentId) {
        return new RecordedNotification(
                NotificationId.newId(),
                customerEmail,
                orderId,
                paymentId,
                NotificationType.ORDER_PAYMENT_AUTHORIZED,
                "Order payment authorized",
                "Your order " + orderId + " payment was authorized.",
                NotificationStatus.RECORDED,
                Instant.now()
        );
    }

    public static RecordedNotification paymentRejected(String customerEmail, String orderId, String paymentId, String reason) {
        String rejectionReason = normalizeOptional(reason);
        String body = "Your order " + orderId + " payment was rejected.";
        if (rejectionReason != null) {
            body = body + " Reason: " + rejectionReason;
        }
        return new RecordedNotification(
                NotificationId.newId(),
                customerEmail,
                orderId,
                paymentId,
                NotificationType.ORDER_PAYMENT_REJECTED,
                "Order payment rejected",
                body,
                NotificationStatus.RECORDED,
                Instant.now()
        );
    }

    public static RecordedNotification rehydrate(NotificationId id,
                                                 String customerEmail,
                                                 String orderId,
                                                 String paymentId,
                                                 NotificationType type,
                                                 String subject,
                                                 String body,
                                                 NotificationStatus status,
                                                 Instant createdAt) {
        return new RecordedNotification(id, customerEmail, orderId, paymentId, type, subject, body, status, createdAt);
    }

    public NotificationId getId() {
        return id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private static NotificationId requireId(NotificationId id) {
        if (id == null) {
            throw new BusinessException(DomainError.validation("Notification id must not be null"));
        }
        return id;
    }

    private static NotificationType requireType(NotificationType type) {
        if (type == null) {
            throw new BusinessException(DomainError.validation("Notification type must not be null"));
        }
        return type;
    }

    private static String requireNotBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation(message));
        }
        return value.trim();
    }

    private static String normalizeOptional(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
