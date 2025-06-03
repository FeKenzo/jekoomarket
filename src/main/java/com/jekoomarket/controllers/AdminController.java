package com.jekoomarket.controllers;

import com.jekoomarket.models.User;
import com.jekoomarket.services.OrderService;
import com.jekoomarket.services.ProductService;
import com.jekoomarket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin") // Changed base mapping
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService; // Autowire ProductService

    @Autowired
    private OrderService orderService; // Autowire OrderService

    // Dashboard mapping
    @GetMapping
    public String adminDashboard(Model model, @AuthenticationPrincipal UserDetails currentUser) {
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

        // Fetch dashboard data
        model.addAttribute("totalUsers", userService.countAllUsers());
        model.addAttribute("totalProducts", productService.countAllProducts());
        model.addAttribute("totalOrders", orderService.countAllOrders());

        return "admin"; // Renders admin.html
    }

    // User management mappings
    @GetMapping("/users") // Path changed
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin-users";
    }

    @PostMapping("/users/add") // Path changed
    public String adicionarUsuario(@RequestParam String email,
                                   @RequestParam String password,
                                   @RequestParam String role,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

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

        redirectAttributes.addFlashAttribute("success", "Usuário adicionado com sucesso!");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}") // Path changed
    public String editUser(@PathVariable Long id, Model model) {
        Optional<User> userOptional = userService.findById(id);
        if (!userOptional.isPresent()) {
            return "redirect:/admin/users";
        }
        model.addAttribute("user", userOptional.get());
        return "edit-user";
    }

    @PostMapping("/users/update") // Path changed
    public String updateUser(@ModelAttribute User userForm,
                             @RequestParam(required = false) String password,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userService.findById(userForm.getId());

        if (!optionalUser.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Usuário não encontrado para atualização.");
            return "redirect:/admin/users";
        }

        User existingUser = optionalUser.get();

        Optional<User> userWithNewEmail = userService.findByEmail(userForm.getEmail());
        if (userWithNewEmail.isPresent() && !userWithNewEmail.get().getId().equals(existingUser.getId())) {
            model.addAttribute("error", "Este e-mail já está em uso por outro usuário.");
            model.addAttribute("user", userForm);
            return "edit-user";
        }
        
        if ("admin@jekoo.com".equals(userForm.getEmail()) && !"admin@jekoo.com".equals(existingUser.getEmail())) {
            model.addAttribute("error", "O e-mail admin@jekoo.com é reservado.");
            model.addAttribute("user", userForm);
            return "edit-user";
        }
        if ("admin@jekoo.com".equals(existingUser.getEmail()) && !existingUser.getEmail().equals(userForm.getEmail())) {
            model.addAttribute("error", "O e-mail do administrador principal (admin@jekoo.com) não pode ser alterado.");
            userForm.setEmail(existingUser.getEmail()); 
            model.addAttribute("user", userForm);
            return "edit-user";
        }

        existingUser.setEmail(userForm.getEmail());
        existingUser.setRole(userForm.getRole());

        if ("admin@jekoo.com".equals(existingUser.getEmail()) && !"ADMIN".equals(userForm.getRole())) {
            model.addAttribute("error", "O administrador principal (admin@jekoo.com) não pode ter sua função alterada de ADMIN.");
            userForm.setRole("ADMIN");
            model.addAttribute("user", userForm);
            return "edit-user";
        }

        if (password != null && !password.trim().isEmpty()) {
            existingUser.setPassword(password);
        }

        userService.save(existingUser);
        redirectAttributes.addFlashAttribute("success", "Usuário atualizado com sucesso!");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}") // Path changed
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.findById(id);

        if (!userOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Usuário não encontrado para exclusão.");
            return "redirect:/admin/users";
        }

        User userToDelete = userOptional.get();
        if ("admin@jekoo.com".equals(userToDelete.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "O usuário administrador principal (admin@jekoo.com) não pode ser excluído.");
        } else {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Usuário excluído com sucesso.");
        }
        return "redirect:/admin/users";
    }
}
