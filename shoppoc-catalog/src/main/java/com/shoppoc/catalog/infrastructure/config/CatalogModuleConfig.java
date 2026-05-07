package com.shoppoc.catalog.infrastructure.config;

import com.shoppoc.catalog.application.CatalogApplicationService;
import com.shoppoc.catalog.domain.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatalogModuleConfig {

    @Bean
    public CatalogApplicationService catalogApplicationService(ProductRepository productRepository) {
        return new CatalogApplicationService(productRepository);
    }
}
