package com.shoppoc.order.infrastructure.web;

import com.shoppoc.order.api.OrderDto;
import com.shoppoc.order.application.CreateOrderCommand;
import com.shoppoc.order.application.CreateOrderLineCommand;
import com.shoppoc.order.application.CreateOrderUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request,
                                     Authentication authentication) {
        List<CreateOrderLineCommand> lines = new ArrayList<CreateOrderLineCommand>();
        for (CreateOrderLineRequest line : request.getLines()) {
            lines.add(new CreateOrderLineCommand(line.getProductId(), line.getQuantity()));
        }

        OrderDto orderDto = createOrderUseCase.createOrder(new CreateOrderCommand(
                authentication.getName(),
                lines
        ));

        return OrderResponse.fromDto(orderDto);
    }
}
