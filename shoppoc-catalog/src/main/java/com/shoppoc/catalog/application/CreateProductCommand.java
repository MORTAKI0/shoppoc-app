package com.shoppoc.catalog.application;

import java.math.BigDecimal;

public class CreateProductCommand {

    private final String sku;
    private final String name;
    private final String description;
    private final BigDecimal priceAmount;
    private final String priceCurrency;
    private final Integer stockQuantity;

    public CreateProductCommand(String sku,
                                String name,
                                String description,
                                BigDecimal priceAmount,
                                String priceCurrency,
                                Integer stockQuantity) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.priceAmount = priceAmount;
        this.priceCurrency = priceCurrency;
        this.stockQuantity = stockQuantity;
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPriceAmount() { return priceAmount; }
    public String getPriceCurrency() { return priceCurrency; }
    public Integer getStockQuantity() { return stockQuantity; }
}
