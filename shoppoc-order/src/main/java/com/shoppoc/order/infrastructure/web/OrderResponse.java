package com.shoppoc.order.infrastructure.web;

import com.shoppoc.order.api.OrderDto;
import com.shoppoc.order.api.OrderLineDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderResponse {

    private final String id;
    private final String customerEmail;
    private final String status;
    private final BigDecimal totalAmount;
    private final String totalCurrency;
    private final List<OrderLineResponse> lines;

    public OrderResponse(String id,
                         String customerEmail,
                         String status,
                         BigDecimal totalAmount,
                         String totalCurrency,
                         List<OrderLineResponse> lines) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.status = status;
        this.totalAmount = totalAmount;
        this.totalCurrency = totalCurrency;
        this.lines = lines;
    }

    public static OrderResponse fromDto(OrderDto dto) {
        List<OrderLineResponse> lineResponses = new ArrayList<OrderLineResponse>();
        for (OrderLineDto line : dto.getLines()) {
            lineResponses.add(OrderLineResponse.fromDto(line));
        }
        return new OrderResponse(
                dto.getId(),
                dto.getCustomerEmail(),
                dto.getStatus(),
                dto.getTotalAmount(),
                dto.getTotalCurrency(),
                lineResponses
        );
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

    public List<OrderLineResponse> getLines() {
        return lines;
    }
}
