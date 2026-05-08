package com.shoppoc.order.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataOrderRepository extends JpaRepository<JpaOrderEntity, String> {

    List<JpaOrderEntity> findAllByOrderByCreatedAtDesc();

    List<JpaOrderEntity> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);
}
