package com.marwaneqada.productapi;

import com.marwaneqada.productapi.domain.Product;
import com.marwaneqada.productapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.seed-data=false")
@AutoConfigureMockMvc
class ProductApiIntegrationTest {

    @Autowired private MockMvc mvc;
    @Autowired private ProductRepository products;

    @BeforeEach
    void resetDatabase() {
        products.deleteAll();
    }

    @Test
    void listsProductsAndAllowsTheAngularOrigin() throws Exception {
        products.save(new Product("Portable Monitor", new BigDecimal("2190.00"), false));

        mvc.perform(get("/api/products").header("Origin", "http://localhost:4200"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"))
                .andExpect(jsonPath("$[0].name").value("Portable Monitor"));
    }

    @Test
    void createsAValidatedProduct() throws Exception {
        mvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"USB Microphone","price":849.90,"selected":false}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("USB Microphone"));

        assertThat(products.count()).isEqualTo(1);

        mvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"price\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatesTheSelectedState() throws Exception {
        Product product = products.save(
                new Product("Webcam", new BigDecimal("680.00"), false));

        mvc.perform(put("/api/products/{id}/selected", product.getId())
                        .param("value", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selected").value(true));

        assertThat(products.findById(product.getId())).get()
                .extracting(Product::isSelected).isEqualTo(true);
    }

    @Test
    void deletesProductsAndReturnsNotFoundForUnknownIds() throws Exception {
        Product product = products.save(
                new Product("Headphones", new BigDecimal("990.00"), false));

        mvc.perform(delete("/api/products/{id}", product.getId()))
                .andExpect(status().isNoContent());
        mvc.perform(delete("/api/products/{id}", product.getId()))
                .andExpect(status().isNotFound());

        assertThat(products.findById(product.getId())).isEmpty();
    }
}
