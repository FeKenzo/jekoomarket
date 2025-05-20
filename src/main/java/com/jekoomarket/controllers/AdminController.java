package com.jekoomarket.controllers;

import com.jekoomarket.models.User;
import com.jekoomarket.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }

    @PostMapping("/add")
    public String adicionarUsuario(@RequestParam String email,
                                   @RequestParam String password,
                                   @RequestParam String role) {
        User novoUsuario = new User();
        novoUsuario.setEmail(email);
        novoUsuario.setPassword(passwordEncoder.encode(password));
        novoUsuario.setRole(role); // Armazena como USER ou ADMIN
        userRepository.save(novoUsuario);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(value -> model.addAttribute("user", value));
        return "edit-user";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User userForm,
                             @RequestParam(required = false) String password) {
        Optional<User> optionalUser = userRepository.findById(userForm.getId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEmail(userForm.getEmail());
            user.setRole(userForm.getRole());

            if (password != null && !password.isBlank()) {
                user.setPassword(passwordEncoder.encode(password));
            }

            userRepository.save(user);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}
