package com.marwaneqada.webapp.config;

import com.marwaneqada.webapp.domain.Product;
import com.marwaneqada.webapp.repository.ProductRepository;
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
    CommandLineRunner loadSampleProducts(ProductRepository products) {
        return args -> products.saveAll(List.of(
                new Product("Ultrabook Pro", new BigDecimal("11499.00"), 7),
                new Product("Mechanical Keyboard", new BigDecimal("649.90"), 18),
                new Product("Wireless Mouse", new BigDecimal("289.00"), 3),
                new Product("27-inch Monitor", new BigDecimal("3199.00"), 9),
                new Product("USB-C Dock", new BigDecimal("899.00"), 12),
                new Product("Noise-cancelling Headset", new BigDecimal("1499.00"), 4),
                new Product("Portable SSD", new BigDecimal("1099.00"), 15),
                new Product("Full HD Webcam", new BigDecimal("539.00"), 6)
        ));
    }
}
