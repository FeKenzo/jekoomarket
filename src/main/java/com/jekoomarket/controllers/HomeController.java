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

        // Adiciona explicitamente o estado de login e o nome do usuário ao modelo
        // Para a página /home, currentUser nunca deve ser null se a segurança estiver correta.
        model.addAttribute("isUserLoggedIn", currentUser != null);
        if (currentUser != null) {
            model.addAttribute("username", currentUser.getUsername());
        } else {
            // Isso não deveria acontecer em uma página protegida como /home
            model.addAttribute("username", "");
        }

        return "home";
    }
}