package com.jekoomarket.controllers;

import com.jekoomarket.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        model.addAttribute("products", productService.findLatest(5));

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
        return "index";
    }

    @GetMapping("/admin")
    public String adminPage(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        // Para o cabeçalho na página de admin
        model.addAttribute("isUserLoggedIn", currentUser != null);
        boolean isAdminRole = false;
        if (currentUser != null) {
            model.addAttribute("username", currentUser.getUsername());
            isAdminRole = currentUser.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        } else {
            // Não deveria chegar aqui se /admin é protegido, mas por segurança
            model.addAttribute("username", "");
        }
        model.addAttribute("isAdmin", isAdminRole);
        return "admin";
    }
}