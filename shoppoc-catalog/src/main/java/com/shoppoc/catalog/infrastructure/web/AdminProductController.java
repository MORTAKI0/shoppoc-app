package com.shoppoc.catalog.infrastructure.web;

import com.shoppoc.catalog.api.ProductDto;
import com.shoppoc.catalog.application.CreateProductCommand;
import com.shoppoc.catalog.application.CreateProductUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/products")
public class AdminProductController {

    private final CreateProductUseCase createProductUseCase;

    public AdminProductController(CreateProductUseCase createProductUseCase) {
        this.createProductUseCase = createProductUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
        ProductDto dto = createProductUseCase.createProduct(new CreateProductCommand(
                request.getSku(),
                request.getName(),
                request.getDescription(),
                request.getPriceAmount(),
                request.getPriceCurrency(),
                request.getStockQuantity()
        ));
        return ProductResponse.fromDto(dto);
    }
}
