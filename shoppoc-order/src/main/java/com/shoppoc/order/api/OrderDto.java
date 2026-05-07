package com.shoppoc.order.api;

import java.math.BigDecimal;
import java.util.List;

public class OrderDto {

    private final String id;
    private final String customerEmail;
    private final String status;
    private final BigDecimal totalAmount;
    private final String totalCurrency;
    private final List<OrderLineDto> lines;

    public OrderDto(String id,
                    String customerEmail,
                    String status,
                    BigDecimal totalAmount,
                    String totalCurrency,
                    List<OrderLineDto> lines) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.status = status;
        this.totalAmount = totalAmount;
        this.totalCurrency = totalCurrency;
        this.lines = lines;
    }

    public String getId() {
        return id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getTotalCurrency() {
        return totalCurrency;
    }

    public List<OrderLineDto> getLines() {
        return lines;
    }
}
