package com.shoppoc.catalog.domain;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<Product> findActiveProducts();

    Optional<Product> findById(ProductId id);

    Optional<Product> findBySku(Sku sku);

    Product save(Product product);
}
