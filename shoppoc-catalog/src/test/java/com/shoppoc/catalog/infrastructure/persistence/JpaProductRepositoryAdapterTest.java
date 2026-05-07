package com.shoppoc.catalog.infrastructure.persistence;

import com.shoppoc.catalog.CatalogTestApplication;
import com.shoppoc.catalog.domain.Product;
import com.shoppoc.catalog.domain.ProductId;
import com.shoppoc.catalog.domain.ProductStatus;
import com.shoppoc.catalog.domain.Sku;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ContextConfiguration(classes = CatalogTestApplication.class)
@Import(JpaProductRepositoryAdapter.class)
class JpaProductRepositoryAdapterTest {

    @Autowired
    private SpringDataProductRepository springDataProductRepository;

    @Autowired
    private JpaProductRepositoryAdapter adapter;

    @Test
    void findActiveProductsReturnsOnlyActiveProducts() {
        springDataProductRepository.save(new JpaProductEntity(
                UUID.randomUUID().toString(),
                "SKU-001",
                "Product A",
                "desc",
                new BigDecimal("10.00"),
                "USD",
                3,
                ProductStatus.ACTIVE
        ));
        springDataProductRepository.save(new JpaProductEntity(
                UUID.randomUUID().toString(),
                "SKU-002",
                "Product B",
                "desc",
                new BigDecimal("11.00"),
                "USD",
                0,
                ProductStatus.INACTIVE
        ));

        List<Product> activeProducts = adapter.findActiveProducts();

        assertEquals(1, activeProducts.size());
        assertEquals("SKU-001", activeProducts.get(0).getSku().getValue());
    }

    @Test
    void findByIdReturnsProductWhenPresent() {
        String id = UUID.randomUUID().toString();
        springDataProductRepository.save(new JpaProductEntity(
                id,
                "SKU-003",
                "Product C",
                "desc",
                new BigDecimal("15.50"),
                "USD",
                8,
                ProductStatus.ACTIVE
        ));

        Optional<Product> product = adapter.findById(ProductId.fromString(id));

        assertTrue(product.isPresent());
        assertEquals("Product C", product.get().getName());
    }

    @Test
    void saveAndFindBySkuWork() {
        Product product = new Product(
                ProductId.newId(),
                Sku.of("sku-tt-1"),
                "Keyboard",
                "Mechanical",
                com.shoppoc.shared.money.Money.of(new BigDecimal("120.00"), "USD"),
                9,
                ProductStatus.ACTIVE
        );

        adapter.save(product);
        Optional<Product> loaded = adapter.findBySku(Sku.of("SKU-TT-1"));

        assertTrue(loaded.isPresent());
        assertEquals("Keyboard", loaded.get().getName());
    }
}
