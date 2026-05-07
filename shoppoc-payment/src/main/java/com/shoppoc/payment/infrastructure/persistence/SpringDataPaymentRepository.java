package com.shoppoc.payment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPaymentRepository extends JpaRepository<JpaPaymentEntity, String> {
}
