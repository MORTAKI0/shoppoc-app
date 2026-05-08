package com.shoppoc.order.application;

import com.shoppoc.order.api.OrderDto;

import java.util.List;

public interface ListCurrentUserOrdersUseCase {

    List<OrderDto> listCurrentUserOrders(String currentUserEmail);
}
