package com.xpanse.cp.product.controller;

import com.xpanse.cp.product.entity.APIResponse;
import com.xpanse.cp.product.entity.Product;
import com.xpanse.cp.product.service.ProductManagementService;
import com.xpanse.cp.product.model.ProductDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProductManagementController {

    private final ProductManagementService productManagementService;

    /**
     * return Product save response
     * @param request contains product details
     */
    @PostMapping
    @Operation(summary = "Create master product")
    @ResponseStatus(HttpStatus.CREATED)
    public APIResponse createProduct(@RequestBody ProductDetails request) {

        return productManagementService.createProduct(request);
    }

    /**
     * @param productId of the product to return
     * @return product using productIdentifier
     */
    @GetMapping("{productId}")
    @ResponseStatus(HttpStatus.OK)
    public Product getProduct(@PathVariable Long productId) {

        return productManagementService.getByProductId(productId);
    }
}
