package com.jekoomarket.controllers;

import com.jekoomarket.models.User;
import com.jekoomarket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute User user, Model model) {
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("user", user); // para manter os dados do formulário
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
    public String userArea() {
        return "user";
    }
}
