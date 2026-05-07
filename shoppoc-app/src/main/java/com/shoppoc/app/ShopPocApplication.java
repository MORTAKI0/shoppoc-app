package com.shoppoc.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.shoppoc")
@EntityScan(basePackages = "com.shoppoc")
@EnableJpaRepositories(basePackages = "com.shoppoc")
public class ShopPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopPocApplication.class, args);
    }
}
