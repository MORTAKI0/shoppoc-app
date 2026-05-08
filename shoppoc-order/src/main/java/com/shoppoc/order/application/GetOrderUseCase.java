package com.shoppoc.order.application;

import com.shoppoc.order.api.OrderDto;

public interface GetOrderUseCase {

    OrderDto getOrder(String orderId, String currentUserEmail);
}
