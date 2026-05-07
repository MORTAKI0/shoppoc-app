package com.shoppoc.catalog.infrastructure.web;

import com.shoppoc.catalog.api.ProductDto;
import com.shoppoc.catalog.api.ProductLookupPort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductLookupPort productLookupPort;

    public ProductController(ProductLookupPort productLookupPort) {
        this.productLookupPort = productLookupPort;
    }

    @GetMapping
    public List<ProductResponse> listProducts() {
        return productLookupPort.listProducts().stream()
                .map(ProductResponse::fromDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable String productId) {
        ProductDto dto = productLookupPort.getProduct(productId);
        return ProductResponse.fromDto(dto);
    }
}
