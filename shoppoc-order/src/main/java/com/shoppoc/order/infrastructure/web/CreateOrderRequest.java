package com.shoppoc.order.infrastructure.web;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class CreateOrderRequest {

    @NotEmpty
    @Valid
    private List<CreateOrderLineRequest> lines;

    public List<CreateOrderLineRequest> getLines() {
        return lines;
    }

    public void setLines(List<CreateOrderLineRequest> lines) {
        this.lines = lines;
    }
}
