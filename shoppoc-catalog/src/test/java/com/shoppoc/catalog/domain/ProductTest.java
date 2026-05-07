package com.shoppoc.catalog.domain;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.money.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductTest {

    @Test
    void rejectsBlankName() {
        assertThrows(BusinessException.class, () -> new Product(
                ProductId.newId(),
                Sku.of("sku-1"),
                " ",
                "desc",
                Money.of(new BigDecimal("10.00"), "USD"),
                1,
                ProductStatus.ACTIVE
        ));
    }

    @Test
    void rejectsNegativeStock() {
        assertThrows(BusinessException.class, () -> new Product(
                ProductId.newId(),
                Sku.of("sku-1"),
                "Name",
                "desc",
                Money.of(new BigDecimal("10.00"), "USD"),
                -1,
                ProductStatus.ACTIVE
        ));
    }

    @Test
    void availableProductWithStockIsAvailable() {
        Product product = new Product(
                ProductId.newId(),
                Sku.of("sku-1"),
                "Name",
                "desc",
                Money.of(new BigDecimal("10.00"), "USD"),
                5,
                ProductStatus.ACTIVE
        );

        assertTrue(product.isAvailable());
        assertTrue(product.hasStock(2));
        assertFalse(product.hasStock(6));
    }
}
