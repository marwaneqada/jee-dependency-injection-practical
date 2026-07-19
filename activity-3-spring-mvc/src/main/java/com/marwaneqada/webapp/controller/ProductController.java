package com.marwaneqada.webapp.controller;

import com.marwaneqada.webapp.domain.Product;
import com.marwaneqada.webapp.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductController {

    private static final int PAGE_SIZE = 5;
    private final ProductRepository products;

    public ProductController(ProductRepository products) {
        this.products = products;
    }

    @GetMapping("/")
    String home() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    String list(@RequestParam(defaultValue = "") String keyword,
                @RequestParam(defaultValue = "0") int page,
                Model model) {
        int safePage = Math.max(page, 0);
        String searchTerm = keyword.trim();
        Page<Product> result = products.findByNameContainingIgnoreCase(
                searchTerm,
                PageRequest.of(safePage, PAGE_SIZE, Sort.by("name").ascending()));

        model.addAttribute("productPage", result);
        model.addAttribute("keyword", searchTerm);
        return "products/list";
    }

    @GetMapping("/products/new")
    String createForm(Model model) {
        model.addAttribute("product", new Product());
        return "products/form";
    }

    @GetMapping("/products/{id}/edit")
    String editForm(@PathVariable Long id, Model model) {
        Product product = products.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        model.addAttribute("product", product);
        return "products/form";
    }

    @PostMapping("/products")
    String save(@Valid @ModelAttribute Product product,
                BindingResult bindingResult,
                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "products/form";
        }

        boolean created = product.getId() == null;
        products.save(product);
        redirectAttributes.addFlashAttribute("message",
                created ? "Product created successfully." : "Product updated successfully.");
        return "redirect:/products";
    }

    @PostMapping("/products/{id}/delete")
    String delete(@PathVariable Long id,
                  @RequestParam(defaultValue = "") String keyword,
                  @RequestParam(defaultValue = "0") int page,
                  RedirectAttributes redirectAttributes) {
        if (!products.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        products.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Product deleted successfully.");
        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("page", Math.max(page, 0));
        return "redirect:/products";
    }
}
