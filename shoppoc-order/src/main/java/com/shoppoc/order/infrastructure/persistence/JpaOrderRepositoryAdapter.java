package com.shoppoc.order.infrastructure.persistence;

import com.shoppoc.order.domain.Order;
import com.shoppoc.order.domain.OrderId;
import com.shoppoc.order.domain.OrderLine;
import com.shoppoc.order.domain.OrderRepository;
import com.shoppoc.order.domain.OrderStatus;
import com.shoppoc.order.domain.Quantity;
import com.shoppoc.shared.money.Money;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaOrderRepositoryAdapter implements OrderRepository {

    private final SpringDataOrderRepository springDataOrderRepository;

    public JpaOrderRepositoryAdapter(SpringDataOrderRepository springDataOrderRepository) {
        this.springDataOrderRepository = springDataOrderRepository;
    }

    @Override
    public Order save(Order order) {
        JpaOrderEntity entity = toEntity(order);
        JpaOrderEntity saved = springDataOrderRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return springDataOrderRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Order> findAll() {
        List<JpaOrderEntity> entities = springDataOrderRepository.findAllByOrderByCreatedAtDesc();
        List<Order> orders = new ArrayList<Order>();
        for (JpaOrderEntity entity : entities) {
            orders.add(toDomain(entity));
        }
        return orders;
    }

    @Override
    public List<Order> findByCustomerEmail(String customerEmail) {
        List<JpaOrderEntity> entities = springDataOrderRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail);
        List<Order> orders = new ArrayList<Order>();
        for (JpaOrderEntity entity : entities) {
            orders.add(toDomain(entity));
        }
        return orders;
    }

    private JpaOrderEntity toEntity(Order order) {
        JpaOrderEntity entity = new JpaOrderEntity();
        entity.setId(order.getId().value());
        entity.setCustomerEmail(order.getCustomerEmail());
        entity.setStatus(order.getStatus().name());
        entity.setPaymentId(order.getPaymentId());
        entity.setPaymentReference(order.getPaymentReference());
        entity.setPaymentStatus(order.getPaymentStatus());
        entity.setPaymentRejectionReason(order.getPaymentRejectionReason());
        entity.setTotalAmount(order.getTotal().getAmount());
        entity.setTotalCurrency(order.getTotal().getCurrency());
        entity.setCreatedAt(order.getCreatedAt());

        List<JpaOrderLineEntity> lines = new ArrayList<JpaOrderLineEntity>();
        for (OrderLine line : order.getLines()) {
            JpaOrderLineEntity lineEntity = new JpaOrderLineEntity();
            lineEntity.setProductId(line.getProductId());
            lineEntity.setSku(line.getSku());
            lineEntity.setProductName(line.getProductName());
            lineEntity.setQuantity(line.getQuantity().value());
            lineEntity.setUnitPriceAmount(line.getUnitPrice().getAmount());
            lineEntity.setUnitPriceCurrency(line.getUnitPrice().getCurrency());
            lineEntity.setLineTotalAmount(line.getLineTotal().getAmount());
            lineEntity.setLineTotalCurrency(line.getLineTotal().getCurrency());
            lines.add(lineEntity);
        }
        entity.setLines(lines);
        return entity;
    }

    private Order toDomain(JpaOrderEntity entity) {
        List<OrderLine> lines = new ArrayList<OrderLine>();
        for (JpaOrderLineEntity lineEntity : entity.getLines()) {
            lines.add(OrderLine.of(
                    lineEntity.getProductId(),
                    lineEntity.getSku(),
                    lineEntity.getProductName(),
                    Quantity.of(lineEntity.getQuantity()),
                    Money.of(lineEntity.getUnitPriceAmount(), lineEntity.getUnitPriceCurrency())
            ));
        }

        return Order.rehydrate(
                OrderId.fromString(entity.getId()),
                entity.getCustomerEmail(),
                lines,
                OrderStatus.valueOf(entity.getStatus()),
                entity.getPaymentId(),
                entity.getPaymentReference(),
                entity.getPaymentStatus(),
                entity.getPaymentRejectionReason(),
                entity.getCreatedAt()
        );
    }
}
