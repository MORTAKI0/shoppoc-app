package com.shoppoc.order.application;

public class CreateOrderLineCommand {

    private final String productId;
    private final int quantity;

    public CreateOrderLineCommand(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
