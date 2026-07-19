package com.marwaneqada.productapi.config;

import com.marwaneqada.productapi.domain.Product;
import com.marwaneqada.productapi.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true", matchIfMissing = true)
public class SampleDataConfiguration {

    @Bean
    CommandLineRunner seedProducts(ProductRepository products) {
        return args -> products.saveAll(List.of(
                new Product("Studio Laptop", new BigDecimal("12990.00"), true),
                new Product("Compact Keyboard", new BigDecimal("590.00"), false),
                new Product("Wireless Trackpad", new BigDecimal("740.00"), false),
                new Product("4K Display", new BigDecimal("4890.00"), true),
                new Product("Desktop Speakers", new BigDecimal("920.00"), false)
        ));
    }
}
