package com.marwaneqada.webapp;

import com.marwaneqada.webapp.domain.Product;
import com.marwaneqada.webapp.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = "app.seed-data=false")
@AutoConfigureMockMvc
class ProductWebApplicationTest {

    @Autowired private MockMvc mvc;
    @Autowired private ProductRepository products;

    @BeforeEach
    void clearProducts() {
        products.deleteAll();
    }

    @Test
    void redirectsAnonymousUsersToLogin() throws Exception {
        mvc.perform(get("/products"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "viewer", roles = "USER")
    void viewerCanBrowseButCannotChangeProducts() throws Exception {
        mvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"));

        mvc.perform(post("/products/1/delete").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void adminReceivesValidationErrorsAndCanCreateAValidProduct() throws Exception {
        mvc.perform(post("/products").with(csrf())
                        .param("name", "x")
                        .param("price", "0")
                        .param("quantity", "-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().attributeHasFieldErrors("product", "name", "price", "quantity"));

        mvc.perform(post("/products").with(csrf())
                        .param("name", "Ergonomic Chair")
                        .param("price", "1899.90")
                        .param("quantity", "6"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        assertThat(products.findAll()).singleElement().satisfies(product -> {
            assertThat(product.getName()).isEqualTo("Ergonomic Chair");
            assertThat(product.getPrice()).isEqualByComparingTo(new BigDecimal("1899.90"));
        });
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void adminCanOpenTheEditForm() throws Exception {
        Product product = products.save(new Product("Desk Lamp", new BigDecimal("249.00"), 8));

        mvc.perform(get("/products/{id}/edit", product.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().attribute("product", hasProperty("id", is(product.getId()))));
    }
}
