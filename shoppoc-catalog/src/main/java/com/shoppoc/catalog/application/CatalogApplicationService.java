package com.shoppoc.catalog.application;

import com.shoppoc.catalog.api.ProductDto;
import com.shoppoc.catalog.api.ProductLookupPort;
import com.shoppoc.catalog.domain.Product;
import com.shoppoc.catalog.domain.ProductId;
import com.shoppoc.catalog.domain.ProductRepository;
import com.shoppoc.catalog.domain.ProductStatus;
import com.shoppoc.catalog.domain.Sku;
import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;
import com.shoppoc.shared.error.NotFoundException;
import com.shoppoc.shared.money.Money;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CatalogApplicationService implements ProductLookupPort, CreateProductUseCase {

    public static final String PRODUCT_SKU_ALREADY_EXISTS = "PRODUCT_SKU_ALREADY_EXISTS";

    private final ProductRepository productRepository;

    public CatalogApplicationService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductDto> listProducts() {
        return productRepository.findActiveProducts().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductDto> findProductById(String productId) {
        ProductId id = ProductId.fromString(productId);
        return productRepository.findById(id).map(this::toDto);
    }

    @Override
    public ProductDto getProduct(String productId) {
        return findProductById(productId)
                .orElseThrow(functionNotFound(productId));
    }

    @Override
    public ProductDto createProduct(CreateProductCommand command) {
        Sku sku = Sku.of(command.getSku());
        if (productRepository.findBySku(sku).isPresent()) {
            throw new BusinessException(DomainError.of(PRODUCT_SKU_ALREADY_EXISTS, "SKU already exists"));
        }

        Product product = new Product(
                ProductId.newId(),
                sku,
                command.getName(),
                command.getDescription(),
                Money.of(command.getPriceAmount(), command.getPriceCurrency()),
                command.getStockQuantity(),
                ProductStatus.ACTIVE
        );
        return toDto(productRepository.save(product));
    }

    private java.util.function.Supplier<NotFoundException> functionNotFound(String productId) {
        return new java.util.function.Supplier<NotFoundException>() {
            @Override
            public NotFoundException get() {
                return new NotFoundException("Product not found: " + productId);
            }
        };
    }

    private ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId().toString(),
                product.getSku().getValue(),
                product.getName(),
                product.getDescription(),
                product.getPrice().getAmount(),
                product.getPrice().getCurrency(),
                product.getStockQuantity(),
                product.getStatus().name()
        );
    }
}
