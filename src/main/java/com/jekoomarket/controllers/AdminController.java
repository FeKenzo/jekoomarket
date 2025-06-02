package com.jekoomarket.controllers;

import com.jekoomarket.models.User;
import com.jekoomarket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin-users";
    }

    @PostMapping("/add")
    public String adicionarUsuario(@RequestParam String email,
                                   @RequestParam String password,
                                   @RequestParam String role,
                                   Model model) {

        if (userService.existsByEmail(email)) {
            model.addAttribute("error", "Este e-mail já está cadastrado.");
            model.addAttribute("users", userService.findAll());
            return "admin-users";
        }

        User novoUsuario = new User();
        novoUsuario.setEmail(email);
        novoUsuario.setPassword(password);
        novoUsuario.setRole(role);
        userService.save(novoUsuario);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        Optional<User> user = userService.findById(id);
        user.ifPresent(value -> model.addAttribute("user", value));
        return "edit-user";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User userForm,
                             @RequestParam(required = false) String password,
                             Model model) {
        Optional<User> optionalUser = userService.findById(userForm.getId());

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();

            // Verificar se o novo email já pertence a outro usuário
            Optional<User> userWithEmail = userService.findByEmail(userForm.getEmail());
            if (userWithEmail.isPresent() && !userWithEmail.get().getId().equals(existingUser.getId())) {
                model.addAttribute("error", "Este e-mail já está em uso.");
                model.addAttribute("user", existingUser);
                return "edit-user";
            }

            existingUser.setEmail(userForm.getEmail());
            existingUser.setRole(userForm.getRole());

            if (password != null && !password.isBlank()) {
                existingUser.setPassword(password);
            }

            userService.save(existingUser);
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin/users";
    }
}
