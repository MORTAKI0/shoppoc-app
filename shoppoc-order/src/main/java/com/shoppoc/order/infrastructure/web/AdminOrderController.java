package com.shoppoc.order.infrastructure.web;

import com.shoppoc.order.api.AdminOrderSummaryDto;
import com.shoppoc.order.application.AdminListOrdersUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {

    private final AdminListOrdersUseCase adminListOrdersUseCase;

    public AdminOrderController(AdminListOrdersUseCase adminListOrdersUseCase) {
        this.adminListOrdersUseCase = adminListOrdersUseCase;
    }

    @GetMapping
    public List<AdminOrderSummaryResponse> listOrders() {
        List<AdminOrderSummaryDto> orders = adminListOrdersUseCase.listAllOrders();
        List<AdminOrderSummaryResponse> responses = new ArrayList<AdminOrderSummaryResponse>();
        for (AdminOrderSummaryDto order : orders) {
            responses.add(AdminOrderSummaryResponse.fromDto(order));
        }
        return responses;
    }
}
