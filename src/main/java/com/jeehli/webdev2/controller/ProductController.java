package com.jeehli.webdev2.controller;
 
import com.jeehli.webdev2.model.Category;
import com.jeehli.webdev2.model.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
 
@Controller
@RequestMapping("/products")
public class ProductController {
 
    private final List<Product> productList = new ArrayList<>();
 
    public ProductController() {
        Category tech = new Category(1L, "Electronics");
        Category books = new Category(2L, "Books");
 
        productList.add(new Product(1L, "Mechanical Keyboard", 3500.00, tech));
        productList.add(new Product(2L, "Wireless Mouse", 1200.00, tech));
        productList.add(new Product(3L, "Spring Boot in Action", 2800.00, books));
    }
 
    // Task 1:
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productList);
        return "products";
    }
 
    // Task 2:
    @GetMapping("/{id}")
    public String getProductDetail(@PathVariable("id") Long id, Model model) {
        Optional<Product> productOpt = productList.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
 
        if (productOpt.isPresent()) {
            model.addAttribute("product", productOpt.get());
            return "product-detail";
        }
        return "redirect:/products";
    }
 
    // Task 4:
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-form";
    }
 
    // Task 4 & 5:
    @PostMapping
    public String createProduct(@ModelAttribute("product") Product product, BindingResult result) {
        // Reject a blank name (in addition to any binding errors such as a non-numeric price)
        if (product.getName() == null || product.getName().isBlank()) {
            result.rejectValue("name", "name.required", "Name is required");
        }
 
        if (result.hasErrors()) {
            return "product-form";
        }
 
        if (product.getCategory() == null) {
            product.setCategory(new Category(1L, "General"));
        }
 
        product.setId((long) (productList.size() + 1));
        productList.add(product);
        return "redirect:/products";
    }
}