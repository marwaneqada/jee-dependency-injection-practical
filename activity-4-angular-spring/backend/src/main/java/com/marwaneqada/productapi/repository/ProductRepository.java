package com.marwaneqada.productapi.repository;

import com.marwaneqada.productapi.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
