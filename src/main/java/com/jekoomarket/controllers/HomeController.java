package com.jekoomarket.controllers;

import com.jekoomarket.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping("/home")
    public String authenticatedHomePage(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        model.addAttribute("products", productService.findLatest(5));

        // Como esta rota é autenticada, currentUser não deve ser null.
        model.addAttribute("isUserLoggedIn", true);
        model.addAttribute("username", currentUser.getUsername());
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        model.addAttribute("isAdmin", isAdmin);

        return "home";
    }
}