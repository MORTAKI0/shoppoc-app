package com.shoppoc.order.api;

import java.math.BigDecimal;

public class OrderLineDto {

    private final String productId;
    private final String sku;
    private final String productName;
    private final int quantity;
    private final BigDecimal unitPriceAmount;
    private final String unitPriceCurrency;
    private final BigDecimal lineTotalAmount;
    private final String lineTotalCurrency;

    public OrderLineDto(String productId,
                        String sku,
                        String productName,
                        int quantity,
                        BigDecimal unitPriceAmount,
                        String unitPriceCurrency,
                        BigDecimal lineTotalAmount,
                        String lineTotalCurrency) {
        this.productId = productId;
        this.sku = sku;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPriceAmount = unitPriceAmount;
        this.unitPriceCurrency = unitPriceCurrency;
        this.lineTotalAmount = lineTotalAmount;
        this.lineTotalCurrency = lineTotalCurrency;
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

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPriceAmount() {
        return unitPriceAmount;
    }

    public String getUnitPriceCurrency() {
        return unitPriceCurrency;
    }

    public BigDecimal getLineTotalAmount() {
        return lineTotalAmount;
    }

    public String getLineTotalCurrency() {
        return lineTotalCurrency;
    }
}
