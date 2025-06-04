package com.jekoomarket.controllers;

import com.jekoomarket.models.Order;
import com.jekoomarket.models.User;
import com.jekoomarket.services.OrderService;
import com.jekoomarket.services.ProductService;
import com.jekoomarket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;

    @Autowired
    public UserController(UserService userService, ProductService productService, OrderService orderService) {
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes) {
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Este e-mail já está cadastrado.");
            return "register";
        }
        user.setRole("USER");
        userService.save(user);
        redirectAttributes.addFlashAttribute("registrationSuccess", "Cadastro realizado com sucesso! Faça o login.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "registrationSuccess", required = false) String registrationSuccess,
                        Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Email ou senha inválidos.");
        }
        if (registrationSuccess != null) {
            // A mensagem será passada para o template pelo RedirectAttributes do controller de registro
            // Mas caso queira adicionar aqui explicitamente via model, pode também:
            // model.addAttribute("messageSuccess", "Cadastro realizado com sucesso! Faça o login.");
        }
        return "login";
    }

    @GetMapping("/user")
    public String userArea(Model model, Authentication authentication, @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) { // Segurança adicional
            return "redirect:/login";
        }
        Optional<User> userOptional = userService.findByEmail(currentUser.getUsername());

        if (!userOptional.isPresent()) {
            return "redirect:/login?error=user_not_found";
        }

        User user = userOptional.get();
        model.addAttribute("userObject", user);
        model.addAttribute("products", productService.findByUser(user.getId()));

        model.addAttribute("isUserLoggedIn", true); // Já que estamos em uma rota autenticada
        model.addAttribute("username", currentUser.getUsername());
        boolean isAdminRole = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        model.addAttribute("isAdmin", isAdminRole);

        return "user";
    }

    @GetMapping("/user/orders")
    public String userOrders(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<User> userOptional = userService.findByEmail(currentUser.getUsername());
        if (!userOptional.isPresent()) {
            return "redirect:/login?error=user_not_found";
        }

        User user = userOptional.get();
        List<Order> orders = orderService.findByUser(user);
        model.addAttribute("orders", orders);

        // Atributos para o header
        model.addAttribute("isUserLoggedIn", true);
        model.addAttribute("username", currentUser.getUsername());
        boolean isAdminRole = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        model.addAttribute("isAdmin", isAdminRole);

        return "my-orders"; // → renderiza o novo arquivo templates/my-orders.html
    }
}
