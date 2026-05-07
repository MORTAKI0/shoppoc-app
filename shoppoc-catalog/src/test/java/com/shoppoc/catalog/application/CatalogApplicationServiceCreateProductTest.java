package com.shoppoc.catalog.application;

import com.shoppoc.catalog.domain.Product;
import com.shoppoc.catalog.domain.ProductRepository;
import com.shoppoc.catalog.domain.Sku;
import com.shoppoc.shared.error.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CatalogApplicationServiceCreateProductTest {

    @Test
    void createProductSuccess() {
        InMemoryProductRepository repository = new InMemoryProductRepository();
        CatalogApplicationService service = new CatalogApplicationService(repository);

        com.shoppoc.catalog.api.ProductDto result = service.createProduct(new CreateProductCommand(
                "SKU-NEW-001",
                "Mouse",
                "Wireless",
                new java.math.BigDecimal("79.00"),
                "EUR",
                12
        ));

        assertEquals("SKU-NEW-001", result.getSku());
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void createProductDuplicateSkuThrowsBusinessException() {
        InMemoryProductRepository repository = new InMemoryProductRepository();
        CatalogApplicationService service = new CatalogApplicationService(repository);
        service.createProduct(new CreateProductCommand("SKU-DUP-001", "Mouse", "Wireless", new java.math.BigDecimal("79.00"), "EUR", 1));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.createProduct(new CreateProductCommand("SKU-DUP-001", "Mouse2", "Wireless", new java.math.BigDecimal("89.00"), "EUR", 2)));

        assertEquals(CatalogApplicationService.PRODUCT_SKU_ALREADY_EXISTS, exception.getDomainError().getCode());
    }

    private static class InMemoryProductRepository implements ProductRepository {
        private final List<Product> items = new ArrayList<Product>();

        @Override
        public List<Product> findActiveProducts() {
            return items;
        }

        @Override
        public Optional<Product> findById(com.shoppoc.catalog.domain.ProductId id) {
            return items.stream().filter(x -> x.getId().toString().equals(id.toString())).findFirst();
        }

        @Override
        public Optional<Product> findBySku(Sku sku) {
            return items.stream().filter(x -> x.getSku().equals(sku)).findFirst();
        }

        @Override
        public Product save(Product product) {
            items.add(product);
            return product;
        }
    }
}
