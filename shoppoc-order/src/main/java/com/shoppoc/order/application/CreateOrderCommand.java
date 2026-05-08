package com.shoppoc.order.application;

import java.util.List;

public class CreateOrderCommand {

    private final String customerEmail;
    private final List<CreateOrderLineCommand> lines;
    private final String paymentMethodToken;

    public CreateOrderCommand(String customerEmail, List<CreateOrderLineCommand> lines, String paymentMethodToken) {
        this.customerEmail = customerEmail;
        this.lines = lines;
        this.paymentMethodToken = paymentMethodToken;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public List<CreateOrderLineCommand> getLines() {
        return lines;
    }

    public String getPaymentMethodToken() {
        return paymentMethodToken;
    }
}
