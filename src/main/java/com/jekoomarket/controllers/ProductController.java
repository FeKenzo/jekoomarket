package com.jekoomarket.controllers;

import com.jekoomarket.models.Product;
import com.jekoomarket.models.User;
import com.jekoomarket.services.ProductService;
import com.jekoomarket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // IMPORTANTE: Adicionar este import
import org.springframework.security.core.userdetails.UserDetails;      // IMPORTANTE: Adicionar este import
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ProductController {

    public static final String UPLOAD_DIR = "product-images";

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping("/products/new")
    public String showAnnounceForm(Model model, @AuthenticationPrincipal UserDetails currentUser) { // Adicionado @AuthenticationPrincipal
        model.addAttribute("product", new Product());

        // Adicionando as informações de login ao modelo para o cabeçalho
        model.addAttribute("isUserLoggedIn", currentUser != null);
        if (currentUser != null) {
            model.addAttribute("username", currentUser.getUsername());
        } else {
            // Como /products/new é uma rota autenticada, currentUser não deveria ser null.
            // Mas, por segurança, para o template:
            model.addAttribute("username", "");
        }
        return "announce-product";
    }

    @PostMapping("/products/save")
    public String saveProduct(@RequestParam("title") String title,
                              @RequestParam("description") String description,
                              @RequestParam("price") Double price,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              Authentication authentication, // Mantido para pegar o usuário que está salvando
                              RedirectAttributes redirectAttributes) {

        // Esta lógica para pegar o User que está salvando o produto está correta.
        // Não usamos @AuthenticationPrincipal aqui porque precisamos do objeto User completo.
        Optional<User> userOptional = userService.findByEmail(authentication.getName());
        if (!userOptional.isPresent()) {
            return "redirect:/login";
        }

        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setUser(userOptional.get());

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String originalFileName = imageFile.getOriginalFilename();
                String fileExtension = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                }
                String newFileName = UUID.randomUUID().toString() + fileExtension;

                Path filePath = uploadPath.resolve(newFileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                product.setImageUrl("/" + UPLOAD_DIR + "/" + newFileName);

            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Falha no upload da imagem.");
                return "redirect:/products/new";
            }
        }

        productService.save(product);
        redirectAttributes.addFlashAttribute("success", "Produto anunciado com sucesso!");
        return "redirect:/user"; // Redireciona para a página do usuário após anunciar
    }
}