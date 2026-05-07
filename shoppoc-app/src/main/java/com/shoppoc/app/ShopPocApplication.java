package com.shoppoc.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.shoppoc")
public class ShopPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopPocApplication.class, args);
    }
}
