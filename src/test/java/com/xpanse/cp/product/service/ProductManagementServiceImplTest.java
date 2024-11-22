package com.xpanse.cp.product.service;

import com.xpanse.cp.product.entity.APIResponse;
import com.xpanse.cp.product.entity.Product;
import com.xpanse.cp.product.exception.DuplicateProductException;
import com.xpanse.cp.product.exception.InvalidRequestException;
import com.xpanse.cp.product.exception.ProductNotFoundException;
import com.xpanse.cp.product.model.ProductDetails;
import com.xpanse.cp.product.repository.ProductManagementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductManagementServiceImplTest {

    @Mock
    private ProductManagementRepository productManagementRepository;

    @InjectMocks
    private ProductManagementServiceImpl productManagementService;

    private ProductDetails validProductDetails;
    private Product savedProduct;

    @BeforeEach
    void setUp() {
        validProductDetails = createValidProductDetails();
        savedProduct = createSavedProduct();
    }

    @Test
    void createProduct_WithValidRequest_ReturnsSuccessResponse() {
        // Arrange
        when(productManagementRepository.existsByProductIdentifier(validProductDetails.getProductIdentifier()))
                .thenReturn(false);
        when(productManagementRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        // Act
        APIResponse response = productManagementService.createProduct(validProductDetails);

        // Assert
        assertNotNull(response);
        assertEquals("Success", response.getStatus());
        assertEquals("Product details saved successfully", response.getMessage());
        verify(productManagementRepository).existsByProductIdentifier(validProductDetails.getProductIdentifier());
        verify(productManagementRepository).save(any(Product.class));
    }

    @Test
    void createProduct_WithDuplicateProductIdentifier_ThrowsDuplicateProductException() {
        // Arrange
        when(productManagementRepository.existsByProductIdentifier(validProductDetails.getProductIdentifier()))
                .thenReturn(true);

        // Act & Assert
        DuplicateProductException exception = assertThrows(DuplicateProductException.class,
                () -> productManagementService.createProduct(validProductDetails));

        assertEquals("Product with id " + validProductDetails.getProductIdentifier() + " already exists",
                exception.getMessage());
        verify(productManagementRepository).existsByProductIdentifier(validProductDetails.getProductIdentifier());
        verify(productManagementRepository, never()).save(any(Product.class));
    }

    @Test
    void createProduct_WithNullRequest_ThrowsInvalidRequestException() {
        // Act & Assert
        assertThrows(InvalidRequestException.class,
                () -> productManagementService.createProduct(null));

        verify(productManagementRepository, never()).existsByProductIdentifier(any());
        verify(productManagementRepository, never()).save(any());
    }

    @Test
    void createProduct_WithEmptyProductName_ThrowsInvalidRequestException() {
        // Arrange
        ProductDetails invalidRequest = createValidProductDetails();
        invalidRequest.setProductName("");

        // Act & Assert
        assertThrows(InvalidRequestException.class,
                () -> productManagementService.createProduct(invalidRequest));

        verify(productManagementRepository, never()).existsByProductIdentifier(any());
        verify(productManagementRepository, never()).save(any());
    }

    @Test
    void createProduct_WithNullEnvironmentSupported_ThrowsInvalidRequestException() {
        // Arrange
        ProductDetails invalidRequest = createValidProductDetails();
        invalidRequest.setEnvironmentsSupported(null);

        // Act & Assert
        assertThrows(InvalidRequestException.class,
                () -> productManagementService.createProduct(invalidRequest));

        verify(productManagementRepository, never()).existsByProductIdentifier(any());
        verify(productManagementRepository, never()).save(any());
    }

    @Test
    void getByProductId_WithExistingId_ReturnsProduct() {
        // Arrange
        Long productId = 1L;
        when(productManagementRepository.findByProductId(productId))
                .thenReturn(Optional.of(savedProduct));

        // Act
        Product result = productManagementService.getByProductId(productId);

        // Assert
        assertNotNull(result);
        assertEquals(savedProduct.getProductId(), result.getProductId());
        assertEquals(savedProduct.getProductName(), result.getProductName());
        verify(productManagementRepository).findByProductId(productId);
    }

    @Test
    void getByProductId_WithNonExistingId_ThrowsProductNotFoundException() {
        // Arrange
        Long nonExistingId = 999L;
        when(productManagementRepository.findByProductId(nonExistingId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                () -> productManagementService.getByProductId(nonExistingId));

        assertEquals("No Product found with id: | {} " + nonExistingId, exception.getMessage());
        verify(productManagementRepository).findByProductId(nonExistingId);
    }

    @Test
    void createProduct_ValidatesAllRequiredFields() {
        // Arrange
        ProductDetails request = new ProductDetails();
        request.setProductName("Test Product"); // Only set product name

        // Act & Assert
        assertThrows(InvalidRequestException.class,
                () -> productManagementService.createProduct(request));

        verify(productManagementRepository, never()).existsByProductIdentifier(any());
        verify(productManagementRepository, never()).save(any());
    }

    private ProductDetails createValidProductDetails() {
        ProductDetails details = new ProductDetails();
        details.setProductName("Test Product");
        details.setProductIdentifier("TEST-001");
        details.setExpirationDate("2024-12-31");
        details.setEnvironmentsSupported(ProductDetails.EnvironmentsSupported.PRODUCTION);
        details.setOwnerEmail("test@example.com");
        details.setAllowedTenant("tenant1");
        return details;
    }

    private Product createSavedProduct() {
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("Test Product");
        product.setProductIdentifier("TEST-001");
        product.setExpirationDate("2024-12-31");
        product.setEnvironmentsSupported("PRODUCTION");
        product.setOwnerEmail("test@example.com");
        product.setAllowedTenant("tenant1");
        return product;
    }
}