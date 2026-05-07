package com.shoppoc.catalog.application;

import com.shoppoc.catalog.api.ProductDto;

public interface CreateProductUseCase {

    ProductDto createProduct(CreateProductCommand command);
}
