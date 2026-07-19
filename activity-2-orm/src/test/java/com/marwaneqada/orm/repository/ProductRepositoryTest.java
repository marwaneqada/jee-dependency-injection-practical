package com.marwaneqada.orm.repository;

import com.marwaneqada.orm.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "app.seed-data=false")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository products;

    @Test
    void performsCrudAndDerivedQueries() {
        Product product = products.save(new Product("Mechanical Keyboard", new BigDecimal("750.00"), 12));

        assertThat(products.findById(product.getId())).isPresent();
        assertThat(products.findByNameContainingIgnoreCase("keyboard"))
                .extracting(Product::getName)
                .containsExactly("Mechanical Keyboard");
        assertThat(products.findByPriceLessThan(new BigDecimal("1000.00"))).hasSize(1);

        product.setQuantity(20);
        products.saveAndFlush(product);
        assertThat(products.findById(product.getId())).get()
                .extracting(Product::getQuantity).isEqualTo(20);

        products.delete(product);
        products.flush();
        assertThat(products.findById(product.getId())).isEmpty();
    }
}
