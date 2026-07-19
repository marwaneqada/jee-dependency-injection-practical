package com.marwaneqada.webapp;

import com.marwaneqada.webapp.domain.Product;
import com.marwaneqada.webapp.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "app.seed-data=false")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository products;

    @Test
    void searchesByNameWithPagination() {
        products.saveAll(List.of(
                new Product("Desk Lamp", new BigDecimal("249.00"), 8),
                new Product("Standing Desk", new BigDecimal("2999.00"), 2),
                new Product("Office Chair", new BigDecimal("1599.00"), 5)
        ));

        var result = products.findByNameContainingIgnoreCase("desk", PageRequest.of(0, 1));

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(1);
    }
}
