package com.shoppoc.catalog.infrastructure.config;

import com.shoppoc.catalog.domain.ProductStatus;
import com.shoppoc.catalog.infrastructure.persistence.JpaProductEntity;
import com.shoppoc.catalog.infrastructure.persistence.SpringDataProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.UUID;

@Configuration
@Profile("local")
public class CatalogLocalDataSeeder {

    @Bean
    CommandLineRunner catalogSeedData(SpringDataProductRepository repository) {
        return args -> {
            seedIfMissing(
                    repository,
                    "SKU-LAPTOP-001",
                    "Developer Laptop",
                    "High-performance laptop for developers",
                    new BigDecimal("1299.00"),
                    "EUR",
                    10,
                    ProductStatus.ACTIVE
            );
            seedIfMissing(
                    repository,
                    "SKU-HEADSET-001",
                    "Noise Canceling Headset",
                    "Comfortable headset for focused work",
                    new BigDecimal("199.00"),
                    "EUR",
                    25,
                    ProductStatus.ACTIVE
            );
            seedIfMissing(
                    repository,
                    "SKU-KEYBOARD-001",
                    "Mechanical Keyboard",
                    "Mechanical keyboard for daily coding",
                    new BigDecimal("149.00"),
                    "EUR",
                    30,
                    ProductStatus.ACTIVE
            );
        };
    }

    private void seedIfMissing(SpringDataProductRepository repository,
                               String sku,
                               String name,
                               String description,
                               BigDecimal priceAmount,
                               String priceCurrency,
                               int stockQuantity,
                               ProductStatus status) {
        if (!repository.findBySku(sku).isPresent()) {
            repository.save(new JpaProductEntity(
                    UUID.randomUUID().toString(),
                    sku,
                    name,
                    description,
                    priceAmount,
                    priceCurrency,
                    stockQuantity,
                    status
            ));
        }
    }
}
