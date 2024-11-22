package com.xpanse.cp.product.service;

import com.xpanse.cp.product.entity.APIResponse;
import com.xpanse.cp.product.entity.Product;
import com.xpanse.cp.product.exception.ProductNotFoundException;
import com.xpanse.cp.product.model.ProductDetails;

public interface ProductManagementService {

    APIResponse createProduct(ProductDetails request);
    Product getByProductId(Long productId) throws ProductNotFoundException;

}

