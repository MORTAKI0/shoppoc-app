package com.shoppoc.catalog.infrastructure.persistence;

import com.shoppoc.catalog.domain.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataProductRepository extends JpaRepository<JpaProductEntity, String> {

    List<JpaProductEntity> findByStatus(ProductStatus status);

    Optional<JpaProductEntity> findBySku(String sku);
}
