package com.xpanse.cp.product.repository;

import com.xpanse.cp.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductManagementRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductId(Long productId);

    boolean existsByProductIdentifier(String productIdentifier);
}
