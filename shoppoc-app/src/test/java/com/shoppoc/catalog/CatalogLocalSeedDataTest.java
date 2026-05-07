package com.shoppoc.catalog;

import com.shoppoc.catalog.api.ProductDto;
import com.shoppoc.catalog.api.ProductLookupPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("local")
class CatalogLocalSeedDataTest {

    @Autowired
    private ProductLookupPort productLookupPort;

    @Test
    void localProfileLoadsCatalogSeedData() {
        List<ProductDto> products = productLookupPort.listProducts();
        Set<String> skus = products.stream()
                .map(ProductDto::getSku)
                .collect(Collectors.toSet());

        assertTrue(products.size() >= 3);
        assertTrue(skus.contains("SKU-LAPTOP-001"));
        assertTrue(skus.contains("SKU-HEADSET-001"));
        assertTrue(skus.contains("SKU-KEYBOARD-001"));
    }
}
