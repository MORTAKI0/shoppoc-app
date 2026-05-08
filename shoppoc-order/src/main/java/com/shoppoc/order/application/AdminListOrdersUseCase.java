package com.shoppoc.order.application;

import com.shoppoc.order.api.AdminOrderSummaryDto;

import java.util.List;

public interface AdminListOrdersUseCase {

    List<AdminOrderSummaryDto> listAllOrders();
}
