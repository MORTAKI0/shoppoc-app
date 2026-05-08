package com.shoppoc.order.domain;

import java.util.Optional;
import java.util.List;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(OrderId id);

    List<Order> findAll();

    List<Order> findByCustomerEmail(String customerEmail);
}
