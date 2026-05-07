package com.shoppoc.order.application;

import com.shoppoc.order.api.OrderDto;

public interface CreateOrderUseCase {

    OrderDto createOrder(CreateOrderCommand command);
}
