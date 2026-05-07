package com.shoppoc.catalog.api;

import java.math.BigDecimal;

public class ProductDto {

    private final String id;
    private final String sku;
    private final String name;
    private final String description;
    private final BigDecimal priceAmount;
    private final String priceCurrency;
    private final int stockQuantity;
    private final String status;

    public ProductDto(String id,
                      String sku,
                      String name,
                      String description,
                      BigDecimal priceAmount,
                      String priceCurrency,
                      int stockQuantity,
                      String status) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.priceAmount = priceAmount;
        this.priceCurrency = priceCurrency;
        this.stockQuantity = stockQuantity;
        this.status = status;
    }

    public String getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPriceAmount() { return priceAmount; }
    public String getPriceCurrency() { return priceCurrency; }
    public int getStockQuantity() { return stockQuantity; }
    public String getStatus() { return status; }
}
