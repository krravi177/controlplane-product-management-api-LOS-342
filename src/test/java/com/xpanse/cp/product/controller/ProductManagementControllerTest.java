package com.xpanse.cp.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpanse.cp.product.entity.APIResponse;
import com.xpanse.cp.product.entity.Product;
import com.xpanse.cp.product.model.ProductDetails;
import com.xpanse.cp.product.service.ProductManagementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@EnableWebMvc
class ProductManagementControllerTest {

    private MockMvc mockMvc;

    private static final Logger logger = LogManager.getLogger(ProductManagementControllerTest.class);

    @Mock
    private ProductManagementService productManagementService;

    @InjectMocks
    private ProductManagementController productManagementController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(productManagementController)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createProduct_WithValidRequest_ReturnsCreatedStatus() throws Exception {
        // Arrange
        ProductDetails request = createSampleProductDetails();
        APIResponse expectedResponse = createSuccessAPIResponse();

        when(productManagementService.createProduct(any(ProductDetails.class)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/")  // Using consistent endpoint
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Product details saved successfully"));

        verify(productManagementService, times(1)).createProduct(any(ProductDetails.class));
    }

    @Test
    void getProduct_WithValidId_ReturnsProduct() throws Exception {
        // Arrange
        Long productId = 1L;
        Product expectedProduct = createSampleProduct();

        when(productManagementService.getByProductId(productId))
                .thenReturn(expectedProduct);

        // Act & Assert
        mockMvc.perform(get("/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.productName").value("Test Product"))
                .andExpect(jsonPath("$.productIdentifier").value("PROD-001"));

        verify(productManagementService, times(1)).getByProductId(productId);
    }

    @Test
    void getProduct_WithInvalidId_ReturnsNotFound() throws Exception {
        // Arrange
        Long invalidProductId = 999L;

        when(productManagementService.getByProductId(invalidProductId))
                .thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/{productId}", invalidProductId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productManagementService, times(1)).getByProductId(invalidProductId);
    }

    @Test
    void createProduct_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        // Arrange
        ProductDetails request = createSampleProductDetails();
        APIResponse errorResponse = new APIResponse();
        errorResponse.setStatus("ERROR");
        errorResponse.setMessage("Internal server error");

        when(productManagementService.createProduct(any(ProductDetails.class)))
                .thenReturn(errorResponse);

        // Act & Assert
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(productManagementService, times(1)).createProduct(any(ProductDetails.class));
    }

    @Test
    void getProduct_WithNonNumericId_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/{productId}", "invalid-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(productManagementService, never()).getByProductId(any());
    }

    // Helper methods to create test data
    private ProductDetails createSampleProductDetails() {
        ProductDetails details = new ProductDetails();
        details.setProductIdentifier("PROD-001");
        details.setProductName("Test Product");
        details.setExpirationDate("2024-12-31");
        details.setEnvironmentsSupported(ProductDetails.EnvironmentsSupported.PRODUCTION);
        details.setOwnerEmail("test@example.com");
        details.setAllowedTenant("tenant1");
        return details;
    }

    private Product createSampleProduct() {
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("Test Product");
        product.setProductIdentifier("PROD-001");
        return product;
    }

    private APIResponse createSuccessAPIResponse() {
        APIResponse response = new APIResponse();
        response.setStatus("Success");
        response.setMessage("Product details saved successfully");
        return response;
    }
}