package com.xpanse.cp.product.service;

import com.xpanse.cp.product.entity.APIResponse;
import com.xpanse.cp.product.entity.Product;
import com.xpanse.cp.product.exception.DuplicateProductException;
import com.xpanse.cp.product.exception.InvalidRequestException;
import com.xpanse.cp.product.exception.ProductNotFoundException;
import com.xpanse.cp.product.model.ProductDetails;
import com.xpanse.cp.product.repository.ProductManagementRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;


@Service
public class ProductManagementServiceImpl implements ProductManagementService {
    /** logger object */
    private static final Logger logger = LogManager.getLogger(ProductManagementServiceImpl.class);
    private final ProductManagementRepository productManagementRepository;

    public ProductManagementServiceImpl(ProductManagementRepository productManagementRepository) {
        this.productManagementRepository = productManagementRepository;
    }
    @Override
    public APIResponse createProduct(ProductDetails request) {
        logger.info("product-management-api | Product details : {} ", request);

        if(isInvalidRequest(request)) {
            throw new InvalidRequestException("Invalid request");
        }

        if (productManagementRepository.existsByProductIdentifier(request.getProductIdentifier())) {
            throw new DuplicateProductException("Product with id " + request.getProductIdentifier() + " already exists");
        }

        Product response = productManagementRepository.save(updateProductFromRequest(request));
        logger.info("product-management-api | Saved product response : {} ", response);
        APIResponse apiResponse = new APIResponse();
        apiResponse.setMessage("Product details saved successfully");
        apiResponse.setStatus("Success");
        return apiResponse;
    }

    @Override
    public Product getByProductId(Long productId) throws ProductNotFoundException {
        return productManagementRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException("No Product found with id: | {} " + productId));
    }

    private Product updateProductFromRequest(ProductDetails request) {
        logger.info("product-management-api | convert : {} ", request);
        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setProductIdentifier(request.getProductIdentifier());
        product.setExpirationDate(request.getExpirationDate());
        product.setEnvironmentsSupported(String.valueOf(request.getEnvironmentsSupported()));
        product.setOwnerEmail(request.getOwnerEmail());
        product.setAllowedTenant(request.getAllowedTenant());
        return product;
    }

    private boolean isInvalidRequest(ProductDetails request) {
        return request == null
                || request.getProductName() == null
                || request.getProductName().trim().isEmpty()
                || request.getProductIdentifier() == null
                || request.getProductIdentifier().trim().isEmpty()
                || request.getEnvironmentsSupported() == null
                || request.getOwnerEmail() == null
                || request.getOwnerEmail().trim().isEmpty();
    }
}

