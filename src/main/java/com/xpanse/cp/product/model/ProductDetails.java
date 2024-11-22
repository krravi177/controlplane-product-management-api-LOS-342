package com.xpanse.cp.product.model;

import com.xpanse.cp.product.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetails {
    private Long productId;
    private String productIdentifier;
    private String productName;
    private String expirationDate;
    private EnvironmentsSupported environmentsSupported;
    private String ownerEmail;
    private String allowedTenant;

    public enum EnvironmentsSupported {
        DEMO, SANDBOX, PRODUCTION
    }

}
