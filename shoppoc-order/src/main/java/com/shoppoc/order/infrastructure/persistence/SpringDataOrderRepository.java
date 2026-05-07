package com.shoppoc.order.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrderRepository extends JpaRepository<JpaOrderEntity, String> {
}
