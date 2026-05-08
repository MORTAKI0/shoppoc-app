package com.shoppoc.order.infrastructure.web;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

public class CreateOrderRequest {

    @NotEmpty
    @Valid
    private List<CreateOrderLineRequest> lines;
    @Pattern(regexp = ".*\\S.*", message = "paymentMethodToken must not be blank")
    private String paymentMethodToken;

    public List<CreateOrderLineRequest> getLines() {
        return lines;
    }

    public void setLines(List<CreateOrderLineRequest> lines) {
        this.lines = lines;
    }

    public String getPaymentMethodToken() {
        return paymentMethodToken;
    }

    public void setPaymentMethodToken(String paymentMethodToken) {
        this.paymentMethodToken = paymentMethodToken;
    }
}
