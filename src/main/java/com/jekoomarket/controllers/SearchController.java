package com.jekoomarket.controllers;

import com.jekoomarket.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    @Autowired
    private ProductService productService;

    @GetMapping("/search")
    public String searchProducts(@RequestParam(value = "query", required = false) String query, Model model, @AuthenticationPrincipal UserDetails currentUser) {
        if (query != null && !query.trim().isEmpty()) {
            model.addAttribute("products", productService.search(query));
        }
        model.addAttribute("query", query);

        model.addAttribute("isUserLoggedIn", currentUser != null);
        boolean isAdmin = false;
        if (currentUser != null) {
            model.addAttribute("username", currentUser.getUsername());
            isAdmin = currentUser.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        } else {
            model.addAttribute("username", "");
        }
        model.addAttribute("isAdmin", isAdmin);
        return "search-results";
    }
}