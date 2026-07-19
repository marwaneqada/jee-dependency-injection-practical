package com.marwaneqada.productapi.controller;

import com.marwaneqada.productapi.domain.Product;
import com.marwaneqada.productapi.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    private final ProductRepository products;

    public ProductController(ProductRepository products) {
        this.products = products;
    }

    @GetMapping
    List<Product> findAll() {
        return products.findAll(Sort.by("id").ascending());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Product create(@Valid @RequestBody Product product) {
        return products.save(new Product(product.getName(), product.getPrice(), product.isSelected()));
    }

    @PutMapping("/{id}/selected")
    Product updateSelected(@PathVariable Long id, @RequestParam boolean value) {
        Product product = findProduct(id);
        product.setSelected(value);
        return products.save(product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) {
        Product product = findProduct(id);
        products.delete(product);
    }

    private Product findProduct(Long id) {
        return products.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }
}
