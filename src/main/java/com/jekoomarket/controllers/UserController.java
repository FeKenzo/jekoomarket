package com.jekoomarket.controllers;

import com.jekoomarket.models.User;
import com.jekoomarket.services.UserService;
import com.jekoomarket.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    // Exibe o formulário de cadastro
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // templates/register.html
    }

    // Processa o formulário e salva o usuário
    @PostMapping("/register")
    public String processRegister(@ModelAttribute User user) {
        user.setRole("USER");
        userServiceImpl.save(user);
        return "redirect:/login";
    }

    // Exibe a tela de login
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Email ou senha inválidos.");
        }
        return "login"; // seu template HTML de login
    }

    @GetMapping("/user")
    public String userArea() {
        return "user"; // Retorna user.html
    }
}
