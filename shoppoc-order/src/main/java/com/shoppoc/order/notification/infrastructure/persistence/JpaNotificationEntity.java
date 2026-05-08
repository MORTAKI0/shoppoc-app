package com.shoppoc.order.notification.infrastructure.persistence;

import com.shoppoc.order.notification.domain.NotificationStatus;
import com.shoppoc.order.notification.domain.NotificationType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "notifications")
public class JpaNotificationEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String orderId;

    private String paymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, length = 2000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    protected JpaNotificationEntity() {
    }

    public JpaNotificationEntity(String id,
                                 String customerEmail,
                                 String orderId,
                                 String paymentId,
                                 NotificationType type,
                                 String subject,
                                 String body,
                                 NotificationStatus status,
                                 Instant createdAt) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.type = type;
        this.subject = subject;
        this.body = body;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
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
}
