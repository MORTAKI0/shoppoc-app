package com.shoppoc.order.application;

import com.shoppoc.catalog.api.ProductDto;
import com.shoppoc.catalog.api.ProductLookupPort;
import com.shoppoc.order.api.OrderDto;
import com.shoppoc.order.api.OrderLineDto;
import com.shoppoc.order.domain.Order;
import com.shoppoc.order.domain.OrderId;
import com.shoppoc.order.domain.OrderLine;
import com.shoppoc.order.domain.OrderRepository;
import com.shoppoc.order.domain.Quantity;
import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;
import com.shoppoc.shared.error.NotFoundException;
import com.shoppoc.shared.money.Money;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderApplicationService implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductLookupPort productLookupPort;

    public OrderApplicationService(OrderRepository orderRepository,
                                   ProductLookupPort productLookupPort) {
        this.orderRepository = orderRepository;
        this.productLookupPort = productLookupPort;
    }

    @Override
    public OrderDto createOrder(CreateOrderCommand command) {
        validateCommand(command);

        List<OrderLine> lines = new ArrayList<OrderLine>();
        for (CreateOrderLineCommand lineCommand : command.getLines()) {
            Quantity quantity = Quantity.of(lineCommand.getQuantity());
            ProductDto product = productLookupPort.findProductById(lineCommand.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found"));

            if (quantity.value() > product.getStockQuantity()) {
                throw new BusinessException(DomainError.of("OUT_OF_STOCK", "Requested quantity exceeds stock"));
            }

            lines.add(OrderLine.of(
                    product.getId(),
                    product.getSku(),
                    product.getName(),
                    quantity,
                    Money.of(product.getPriceAmount(), product.getPriceCurrency())
            ));
        }

        Order order = Order.create(OrderId.newId(), command.getCustomerEmail(), lines);
        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    private void validateCommand(CreateOrderCommand command) {
        if (command == null) {
            throw new BusinessException(DomainError.validation("Command must not be null"));
        }
        if (command.getCustomerEmail() == null || command.getCustomerEmail().trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Customer email must not be blank"));
        }
        if (command.getLines() == null || command.getLines().isEmpty()) {
            throw new BusinessException(DomainError.validation("Order lines must not be empty"));
        }
    }

    private OrderDto toDto(Order order) {
        List<OrderLineDto> lineDtos = new ArrayList<OrderLineDto>();
        for (OrderLine line : order.getLines()) {
            lineDtos.add(new OrderLineDto(
                    line.getProductId(),
                    line.getSku(),
                    line.getProductName(),
                    line.getQuantity().value(),
                    line.getUnitPrice().getAmount(),
                    line.getUnitPrice().getCurrency(),
                    line.getLineTotal().getAmount(),
                    line.getLineTotal().getCurrency()
            ));
        }

        return new OrderDto(
                order.getId().value(),
                order.getCustomerEmail(),
                order.getStatus().name(),
                order.getTotal().getAmount(),
                order.getTotal().getCurrency(),
                lineDtos
        );
    }
}
