package com.jekoomarket.controllers;

import com.jekoomarket.models.Product;
import com.jekoomarket.models.User;
import com.jekoomarket.services.OrderService;
import com.jekoomarket.services.ProductService;
import com.jekoomarket.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/checkout")
@SessionAttributes({"checkoutProduct", "checkoutQuantity", "checkoutAddress"})
public class CheckoutController {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    private void addUserAuthAttributesToModel(Model model, UserDetails currentUser) {
        model.addAttribute("isUserLoggedIn", currentUser != null);
        boolean isAdmin = false;
        if (currentUser != null) {
            model.addAttribute("username", currentUser.getUsername());
            isAdmin = currentUser.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        } else {
            model.addAttribute("username", "");
        }
        model.addAttribute("isAdmin", isAdmin);
    }

    @GetMapping("/start/{productId}")
    public String startCheckout(@PathVariable Long productId, Model model, @AuthenticationPrincipal UserDetails currentUser, RedirectAttributes redirectAttributes) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        addUserAuthAttributesToModel(model, currentUser);

        Optional<Product> productOptional = productService.findById(productId);
        if (!productOptional.isPresent()) { // Java 8 compatible
            logger.warn("Tentativa de checkout para produto não existente: ID {}", productId);
            redirectAttributes.addFlashAttribute("error", "Produto não encontrado.");
            return "redirect:/";
        }
        Product product = productOptional.get();

        Optional<User> userOptional = userService.findByEmail(currentUser.getUsername());
        if (!userOptional.isPresent()) { // Java 8 compatible
            return "redirect:/login";
        }
        User user = userOptional.get();

        model.addAttribute("product", product);
        model.addAttribute("userAddress", user);
        model.addAttribute("quantity", 1);

        model.addAttribute("checkoutProduct", product);
        model.addAttribute("checkoutQuantity", 1);

        // Pre-popula checkoutAddress com os dados do usuário, se disponíveis
        Map<String, String> initialAddress = new HashMap<>();
        initialAddress.put("cep", user.getCep());
        initialAddress.put("logradouro", user.getLogradouro());
        initialAddress.put("numero", user.getNumero());
        initialAddress.put("complemento", user.getComplemento());
        initialAddress.put("bairro", user.getBairro());
        initialAddress.put("cidade", user.getCidade());
        initialAddress.put("estado", user.getEstado());
        model.addAttribute("checkoutAddress", initialAddress);


        return "confirm-address";
    }

    @PostMapping("/address")
    public String confirmAddress(@ModelAttribute("checkoutProduct") Product product,
                                 @RequestParam("quantity") int quantity,
                                 @RequestParam("cep") String cep,
                                 @RequestParam("logradouro") String logradouro,
                                 @RequestParam("numero") String numero,
                                 @RequestParam(value = "complemento", required = false) String complemento,
                                 @RequestParam("bairro") String bairro,
                                 @RequestParam("cidade") String cidade,
                                 @RequestParam("estado") String estado,
                                 Model model, @AuthenticationPrincipal UserDetails currentUser) {

        addUserAuthAttributesToModel(model, currentUser);

        Map<String, String> addressMap = new HashMap<>();
        addressMap.put("cep", cep);
        addressMap.put("logradouro", logradouro);
        addressMap.put("numero", numero);
        addressMap.put("complemento", (complemento != null ? complemento : ""));
        addressMap.put("bairro", bairro);
        addressMap.put("cidade", cidade);
        addressMap.put("estado", estado);

        model.addAttribute("checkoutQuantity", quantity);
        model.addAttribute("checkoutAddress", addressMap);

        // Para exibir na página de pagamento
        model.addAttribute("product", product);
        double total = product.getPrice() * quantity;
        model.addAttribute("totalAmount", total);

        return "select-payment";
    }

    @PostMapping("/place-order")
    public String placeOrder(@ModelAttribute("checkoutProduct") Product product,
                             @ModelAttribute("checkoutQuantity") int quantity,
                             @ModelAttribute("checkoutAddress") Map<String, String> deliveryAddress,
                             @RequestParam("paymentMethod") String paymentMethod,
                             @AuthenticationPrincipal UserDetails currentUser,
                             SessionStatus sessionStatus,
                             RedirectAttributes redirectAttributes) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        Optional<User> userOptional = userService.findByEmail(currentUser.getUsername());
        if (!userOptional.isPresent()) { // Java 8 compatible
            return "redirect:/login";
        }
        User user = userOptional.get();

        try {
            logger.info("Finalizando pedido para Usuário: {}, Produto: {}, Qtd: {}, Pagamento: {}", user.getEmail(), product.getTitle(), quantity, paymentMethod);
            orderService.placeOrder(user, product, quantity, deliveryAddress, paymentMethod);
            sessionStatus.setComplete();
            redirectAttributes.addFlashAttribute("successMessage", "Pedido realizado com sucesso!");
            return "redirect:/checkout/success";
        } catch (Exception e) {
            logger.error("Erro ao finalizar pedido para usuário {}: {}", user.getEmail(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao processar seu pedido. Tente novamente.");
            return "redirect:/checkout/start/" + product.getId();
        }
    }

    @GetMapping("/success")
    public String orderSuccess(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        addUserAuthAttributesToModel(model, currentUser);
        return "order-success";
    }
}