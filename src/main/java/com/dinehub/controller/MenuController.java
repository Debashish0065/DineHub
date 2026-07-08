package com.dinehub.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dinehub.entity.Product;
import com.dinehub.repository.ProductRepository;

@Controller
public class MenuController {

    private final ProductRepository productRepository;

    public MenuController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/menu")
    public String menuPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        // Clean keyword
        if (keyword != null) {
            keyword = keyword.trim().toLowerCase();

            if (keyword.isEmpty()) {
                keyword = null;
            } else if (keyword.endsWith("s")) {
                // burgers -> burger
                // pizzas -> pizza
                keyword = keyword.substring(0, keyword.length() - 1);
            }
        }

        // Clean category
        if (category != null) {
            category = category.trim();

            if (category.isEmpty()) {
                category = null;
            }
        }

        Pageable pageable = PageRequest.of(page, 12);

        Page<Product> productPage =
                productRepository.searchProducts(keyword, category, pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);

        return "menu";
    }

}