package com.shoppoc.order.infrastructure.web;

import com.shoppoc.order.api.OrderDto;
import com.shoppoc.order.application.CreateOrderCommand;
import com.shoppoc.order.application.CreateOrderLineCommand;
import com.shoppoc.order.application.CreateOrderUseCase;
import com.shoppoc.order.application.GetOrderUseCase;
import com.shoppoc.order.application.ListCurrentUserOrdersUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final GetOrderUseCase getOrderUseCase;
    private final ListCurrentUserOrdersUseCase listCurrentUserOrdersUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                           GetOrderUseCase getOrderUseCase,
                           ListCurrentUserOrdersUseCase listCurrentUserOrdersUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.listCurrentUserOrdersUseCase = listCurrentUserOrdersUseCase;
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
                lines,
                request.getPaymentMethodToken()
        ));

        return OrderResponse.fromDto(orderDto);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable String orderId, Authentication authentication) {
        OrderDto orderDto = getOrderUseCase.getOrder(orderId, authentication.getName());
        return OrderResponse.fromDto(orderDto);
    }

    @GetMapping
    public List<OrderResponse> listMyOrders(Authentication authentication) {
        List<OrderDto> orders = listCurrentUserOrdersUseCase.listCurrentUserOrders(authentication.getName());
        List<OrderResponse> responses = new ArrayList<OrderResponse>();
        for (OrderDto order : orders) {
            responses.add(OrderResponse.fromDto(order));
        }
        return responses;
    }
}
