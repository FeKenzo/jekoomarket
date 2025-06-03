package com.jekoomarket.controllers;

import com.jekoomarket.models.Product;
import com.jekoomarket.models.User;
import com.jekoomarket.services.ProductService;
import com.jekoomarket.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    public static final String UPLOAD_DIR = "product-images";

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping("/products/new")
    public String showAnnounceForm(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        model.addAttribute("product", new Product());

        model.addAttribute("isUserLoggedIn", true); // Rota autenticada
        model.addAttribute("username", currentUser.getUsername());
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        model.addAttribute("isAdmin", isAdmin);

        return "announce-product";
    }

    @PostMapping("/products/save")
    public String saveProduct(@RequestParam("title") String title,
                              @RequestParam("description") String description,
                              @RequestParam("price") Double price,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {

        logger.info("Tentando salvar produto. Título: {}", title);

        Optional<User> userOptional = userService.findByEmail(authentication.getName());
        if (!userOptional.isPresent()) {
            logger.warn("Usuário não autenticado tentou salvar produto: {}", authentication.getName());
            return "redirect:/login";
        }

        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setUser(userOptional.get());
        logger.info("Objeto Produto criado. Usuário: {}", userOptional.get().getEmail());

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
                logger.info("Imagem salva em: {}", product.getImageUrl());

            } catch (IOException e) {
                logger.error("Falha no upload da imagem para o produto: {}", title, e);
                redirectAttributes.addFlashAttribute("error", "Falha no upload da imagem: " + e.getMessage());
                return "redirect:/products/new";
            }
        } else {
            logger.warn("Nenhuma imagem fornecida para o produto: {}", title);
        }

        try {
            productService.save(product);
            redirectAttributes.addFlashAttribute("success", "Produto anunciado com sucesso!");
        } catch (Exception e) {
            logger.error("Erro ao salvar produto no banco de dados: {}. Erro: {}", title, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Erro ao salvar produto. Verifique os logs ou tente novamente.");
            return "redirect:/products/new";
        }
        return "redirect:/user";
    }
}