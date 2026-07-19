package com.marwaneqada.orm.repository;

import com.marwaneqada.orm.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findByPriceLessThan(BigDecimal maximumPrice);
}
