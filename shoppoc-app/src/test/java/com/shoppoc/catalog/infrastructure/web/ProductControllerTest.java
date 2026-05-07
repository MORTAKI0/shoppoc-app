package com.shoppoc.catalog.infrastructure.web;

import com.shoppoc.app.web.GlobalExceptionHandler;
import com.shoppoc.catalog.api.ProductDto;
import com.shoppoc.catalog.api.ProductLookupPort;
import com.shoppoc.shared.error.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductLookupPort productLookupPort;

    @Test
    void listProductsReturns200() throws Exception {
        when(productLookupPort.listProducts()).thenReturn(Collections.singletonList(new ProductDto(
                "9a2945f0-fa5a-4b46-98a6-4f63144de571",
                "SKU-100",
                "Phone",
                "Smart phone",
                new BigDecimal("599.99"),
                "USD",
                4,
                "ACTIVE"
        )));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$[0].sku").value("SKU-100"));
    }

    @Test
    void getProductReturns200() throws Exception {
        when(productLookupPort.getProduct(anyString())).thenReturn(new ProductDto(
                "9a2945f0-fa5a-4b46-98a6-4f63144de571",
                "SKU-100",
                "Phone",
                "Smart phone",
                new BigDecimal("599.99"),
                "USD",
                4,
                "ACTIVE"
        ));

        mockMvc.perform(get("/api/v1/products/9a2945f0-fa5a-4b46-98a6-4f63144de571"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    @Test
    void getProductReturns404WhenMissing() throws Exception {
        when(productLookupPort.getProduct(anyString())).thenThrow(new NotFoundException("Product not found"));

        mockMvc.perform(get("/api/v1/products/9a2945f0-fa5a-4b46-98a6-4f63144de571"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }
}
