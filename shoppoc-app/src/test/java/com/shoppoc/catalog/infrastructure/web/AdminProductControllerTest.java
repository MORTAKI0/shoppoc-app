package com.shoppoc.catalog.infrastructure.web;

import com.shoppoc.app.web.GlobalExceptionHandler;
import com.shoppoc.catalog.api.ProductDto;
import com.shoppoc.catalog.application.CreateProductUseCase;
import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateProductUseCase createProductUseCase;

    @Test
    void createReturnsCreated() throws Exception {
        when(createProductUseCase.createProduct(any())).thenReturn(new ProductDto(
                "a33c5f12-6f9a-47ce-85b0-8e73ee0bf9c4",
                "SKU-MOUSE-001",
                "Wireless Mouse",
                "Wireless mouse for daily work",
                new BigDecimal("79.00"),
                "EUR",
                40,
                "ACTIVE"
        ));

        mockMvc.perform(post("/api/v1/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"SKU-MOUSE-001\",\"name\":\"Wireless Mouse\",\"description\":\"Wireless mouse for daily work\",\"priceAmount\":79.00,\"priceCurrency\":\"EUR\",\"stockQuantity\":40}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("SKU-MOUSE-001"));
    }

    @Test
    void createReturnsBadRequestWhenInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"\",\"name\":\"\",\"priceAmount\":-1,\"priceCurrency\":\"\",\"stockQuantity\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void createReturnsConflictForDuplicateSku() throws Exception {
        when(createProductUseCase.createProduct(any())).thenThrow(
                new BusinessException(DomainError.of("PRODUCT_SKU_ALREADY_EXISTS", "SKU already exists"))
        );

        mockMvc.perform(post("/api/v1/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"SKU-MOUSE-001\",\"name\":\"Wireless Mouse\",\"description\":\"Wireless mouse for daily work\",\"priceAmount\":79.00,\"priceCurrency\":\"EUR\",\"stockQuantity\":40}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("PRODUCT_SKU_ALREADY_EXISTS"));
    }
}
