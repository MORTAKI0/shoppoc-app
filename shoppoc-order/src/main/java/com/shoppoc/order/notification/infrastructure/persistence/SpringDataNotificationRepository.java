package com.shoppoc.order.notification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataNotificationRepository extends JpaRepository<JpaNotificationEntity, String> {

    List<JpaNotificationEntity> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);

    List<JpaNotificationEntity> findByOrderIdOrderByCreatedAtDesc(String orderId);
}
