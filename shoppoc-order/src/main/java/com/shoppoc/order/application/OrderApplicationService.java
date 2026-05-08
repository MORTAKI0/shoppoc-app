package com.shoppoc.order.application;

import com.shoppoc.catalog.api.ProductDto;
import com.shoppoc.catalog.api.ProductLookupPort;
import com.shoppoc.order.api.AdminOrderSummaryDto;
import com.shoppoc.order.api.OrderDto;
import com.shoppoc.order.api.OrderLineDto;
import com.shoppoc.order.notification.application.NotificationRecorder;
import com.shoppoc.payment.api.PaymentAuthorizationPort;
import com.shoppoc.payment.api.PaymentAuthorizationRequest;
import com.shoppoc.payment.api.PaymentDto;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderApplicationService implements CreateOrderUseCase, GetOrderUseCase, ListCurrentUserOrdersUseCase, AdminListOrdersUseCase {

    private final OrderRepository orderRepository;
    private final ProductLookupPort productLookupPort;
    private final PaymentAuthorizationPort paymentAuthorizationPort;
    private final NotificationRecorder notificationRecorder;

    public OrderApplicationService(OrderRepository orderRepository,
                                   ProductLookupPort productLookupPort,
                                   PaymentAuthorizationPort paymentAuthorizationPort,
                                   NotificationRecorder notificationRecorder) {
        this.orderRepository = orderRepository;
        this.productLookupPort = productLookupPort;
        this.paymentAuthorizationPort = paymentAuthorizationPort;
        this.notificationRecorder = notificationRecorder;
    }

    @Override
    @Transactional
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
        Order savedInitial = orderRepository.save(order);

        String paymentMethodToken = command.getPaymentMethodToken() == null || command.getPaymentMethodToken().trim().isEmpty()
                ? "stub-ok"
                : command.getPaymentMethodToken().trim();

        PaymentDto payment = paymentAuthorizationPort.authorize(new PaymentAuthorizationRequest(
                savedInitial.getTotal().getAmount(),
                savedInitial.getTotal().getCurrency(),
                savedInitial.getId().value(),
                paymentMethodToken
        ));

        Order paymentUpdated;
        if ("AUTHORIZED".equalsIgnoreCase(payment.getStatus())) {
            paymentUpdated = savedInitial.markPaid(payment.getId(), payment.getReference());
        } else if ("REJECTED".equalsIgnoreCase(payment.getStatus())) {
            paymentUpdated = savedInitial.markPaymentRejected(payment.getId(), payment.getReference(), payment.getRejectionReason());
        } else {
            throw new BusinessException(DomainError.conflict("Unsupported payment status: " + payment.getStatus()));
        }

        Order saved = orderRepository.save(paymentUpdated);
        if ("AUTHORIZED".equalsIgnoreCase(payment.getStatus())) {
            notificationRecorder.recordPaymentAuthorized(saved.getCustomerEmail(), saved.getId().value(), payment.getId());
        } else {
            notificationRecorder.recordPaymentRejected(saved.getCustomerEmail(), saved.getId().value(), payment.getId(), payment.getRejectionReason());
        }
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrder(String orderId, String currentUserEmail) {
        if (currentUserEmail == null || currentUserEmail.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Current user email must not be blank"));
        }

        OrderId id = OrderId.fromString(orderId);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (!order.belongsTo(currentUserEmail.trim())) {
            throw new AccessDeniedException("Order access denied");
        }

        return toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> listCurrentUserOrders(String currentUserEmail) {
        if (currentUserEmail == null || currentUserEmail.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Current user email must not be blank"));
        }

        List<Order> orders = orderRepository.findByCustomerEmail(currentUserEmail.trim());
        List<OrderDto> result = new ArrayList<OrderDto>();
        for (Order order : orders) {
            result.add(toDto(order));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminOrderSummaryDto> listAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<AdminOrderSummaryDto> result = new ArrayList<AdminOrderSummaryDto>();
        for (Order order : orders) {
            result.add(new AdminOrderSummaryDto(
                    order.getId().value(),
                    order.getCustomerEmail(),
                    order.getStatus().name(),
                    order.getTotal().getAmount(),
                    order.getTotal().getCurrency(),
                    DateTimeFormatter.ISO_INSTANT.format(order.getCreatedAt()),
                    order.getPaymentStatus(),
                    order.getPaymentId(),
                    order.getPaymentReference()
            ));
        }
        return result;
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
        if (command.getPaymentMethodToken() != null && command.getPaymentMethodToken().trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Payment method token must not be blank"));
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
                order.getPaymentId(),
                order.getPaymentReference(),
                order.getPaymentStatus(),
                order.getPaymentRejectionReason(),
                order.getTotal().getAmount(),
                order.getTotal().getCurrency(),
                lineDtos
        );
    }
}
