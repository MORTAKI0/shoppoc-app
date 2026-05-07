package com.shoppoc.order.domain;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;
import com.shoppoc.shared.money.Money;

import java.util.Objects;

public final class OrderLine {

    private final String productId;
    private final String sku;
    private final String productName;
    private final Quantity quantity;
    private final Money unitPrice;
    private final Money lineTotal;

    private OrderLine(String productId,
                      String sku,
                      String productName,
                      Quantity quantity,
                      Money unitPrice) {
        this.productId = requireText(productId, "Product id must not be blank");
        this.sku = requireText(sku, "Sku must not be blank");
        this.productName = requireText(productName, "Product name must not be blank");
        this.quantity = Objects.requireNonNull(quantity, "quantity");
        this.unitPrice = Objects.requireNonNull(unitPrice, "unitPrice");
        this.lineTotal = unitPrice.multiply(quantity.value());
    }

    public static OrderLine of(String productId,
                               String sku,
                               String productName,
                               Quantity quantity,
                               Money unitPrice) {
        return new OrderLine(productId, sku, productName, quantity, unitPrice);
    }

    public String getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }

    public String getProductName() {
        return productName;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public Money getLineTotal() {
        return lineTotal;
    }

    private String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation(message));
        }
        return value.trim();
    }
}
