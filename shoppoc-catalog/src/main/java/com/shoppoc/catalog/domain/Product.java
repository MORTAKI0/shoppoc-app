package com.shoppoc.catalog.domain;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;
import com.shoppoc.shared.money.Money;

public class Product {

    private final ProductId id;
    private final Sku sku;
    private final String name;
    private final String description;
    private final Money price;
    private final int stockQuantity;
    private final ProductStatus status;

    public Product(ProductId id,
                   Sku sku,
                   String name,
                   String description,
                   Money price,
                   int stockQuantity,
                   ProductStatus status) {
        if (id == null) {
            throw new BusinessException(DomainError.validation("Product id must not be null"));
        }
        if (sku == null) {
            throw new BusinessException(DomainError.validation("SKU must not be null"));
        }
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Product name must not be blank"));
        }
        if (price == null) {
            throw new BusinessException(DomainError.validation("Price must not be null"));
        }
        if (stockQuantity < 0) {
            throw new BusinessException(DomainError.validation("Stock quantity must not be negative"));
        }
        if (status == null) {
            throw new BusinessException(DomainError.validation("Status must not be null"));
        }
        this.id = id;
        this.sku = sku;
        this.name = name.trim();
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = status;
    }

    public boolean isAvailable() {
        return ProductStatus.ACTIVE.equals(status) && stockQuantity > 0;
    }

    public boolean hasStock(int requestedQuantity) {
        return requestedQuantity > 0 && stockQuantity >= requestedQuantity;
    }

    public ProductId getId() { return id; }
    public Sku getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Money getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public ProductStatus getStatus() { return status; }
}
