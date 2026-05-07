package com.shoppoc.order.application;

import java.util.List;

public class CreateOrderCommand {

    private final String customerEmail;
    private final List<CreateOrderLineCommand> lines;

    public CreateOrderCommand(String customerEmail, List<CreateOrderLineCommand> lines) {
        this.customerEmail = customerEmail;
        this.lines = lines;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public List<CreateOrderLineCommand> getLines() {
        return lines;
    }
}
