package com.shoppoc.catalog.infrastructure.persistence;

import com.shoppoc.catalog.domain.Product;
import com.shoppoc.catalog.domain.ProductId;
import com.shoppoc.catalog.domain.ProductRepository;
import com.shoppoc.catalog.domain.ProductStatus;
import com.shoppoc.catalog.domain.Sku;
import com.shoppoc.shared.money.Money;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaProductRepositoryAdapter implements ProductRepository {

    private final SpringDataProductRepository springDataProductRepository;

    public JpaProductRepositoryAdapter(SpringDataProductRepository springDataProductRepository) {
        this.springDataProductRepository = springDataProductRepository;
    }

    @Override
    public List<Product> findActiveProducts() {
        return springDataProductRepository.findByStatus(ProductStatus.ACTIVE).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        return springDataProductRepository.findById(id.toString()).map(this::toDomain);
    }

    private Product toDomain(JpaProductEntity entity) {
        return new Product(
                ProductId.fromString(entity.getId()),
                Sku.of(entity.getSku()),
                entity.getName(),
                entity.getDescription(),
                Money.of(entity.getPriceAmount(), entity.getPriceCurrency()),
                entity.getStockQuantity(),
                entity.getStatus()
        );
    }
}
