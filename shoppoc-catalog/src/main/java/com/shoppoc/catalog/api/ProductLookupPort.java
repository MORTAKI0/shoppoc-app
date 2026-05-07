package com.shoppoc.catalog.api;

import java.util.List;
import java.util.Optional;

public interface ProductLookupPort {

    List<ProductDto> listProducts();

    Optional<ProductDto> findProductById(String productId);

    ProductDto getProduct(String productId);
}
