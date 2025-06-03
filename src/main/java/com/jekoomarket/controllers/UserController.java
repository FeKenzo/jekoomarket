package com.jekoomarket.controllers;

import com.jekoomarket.models.User;
import com.jekoomarket.services.ProductService; // IMPORTANTE: Adicionar este import
import com.jekoomarket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // Usado no register
import org.springframework.web.bind.annotation.PostMapping;   // Usado no register
import org.springframework.web.bind.annotation.RequestParam;  // Usado no login

import java.util.Collections; // Para lista vazia, se necessário
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;
    private final ProductService productService; // 1. Declaração do campo ProductService

    @Autowired
    // 2. ProductService adicionado ao construtor
    public UserController(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService; // Atribuição do ProductService
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute User user, Model model) {
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Este e-mail já está cadastrado.");
            return "register";
        }

        user.setRole("USER");
        userService.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Email ou senha inválidos.");
        }
        return "login";
    }

    @GetMapping("/user")
    public String userArea(Model model, Authentication authentication, @AuthenticationPrincipal UserDetails currentUser) {
        Optional<User> userOptional = userService.findByEmail(authentication.getName());

        if (!userOptional.isPresent()) { // Compatível com Java 8
            // Lógica caso o usuário autenticado não seja encontrado no banco,
            // o que não deveria acontecer. Redirecionar para login talvez?
            return "redirect:/login?error=user_not_found";
        }

        User user = userOptional.get();
        model.addAttribute("userObject", user); // Adiciona o objeto User ao modelo (renomeado para evitar conflito com o 'user' do register)

        // 3. Usando o productService para buscar produtos do usuário e adicionando ao model
        model.addAttribute("products", productService.findByUser(user.getId()));

        // Adicionando atributos para o cabeçalho
        model.addAttribute("isUserLoggedIn", currentUser != null);
        boolean isAdminRole = false;
        if (currentUser != null) {
            model.addAttribute("username", currentUser.getUsername());
            isAdminRole = currentUser.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        } else {
            model.addAttribute("username", "");
        }
        model.addAttribute("isAdmin", isAdminRole);

        return "user"; // Renderiza a página user.html
    }
}