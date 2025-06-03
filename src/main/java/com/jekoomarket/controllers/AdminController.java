package com.jekoomarket.controllers;

import com.jekoomarket.models.User;
import com.jekoomarket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // IMPORTANTE: Adicionar este import

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
                                   Model model, // Model é para renderizar a view diretamente
                                   RedirectAttributes redirectAttributes) { // RedirectAttributes para após redirect

        if (userService.existsByEmail(email)) {
            // Se houver erro e formos renderizar a mesma página, usamos Model
            model.addAttribute("error", "Este e-mail já está cadastrado.");
            model.addAttribute("users", userService.findAll()); // Para repopular a lista
            return "admin-users"; // Renderiza a página novamente com o erro
        }

        User novoUsuario = new User();
        novoUsuario.setEmail(email);
        novoUsuario.setPassword(password); // A senha será encriptada pelo UserService
        novoUsuario.setRole(role);
        userService.save(novoUsuario);

        redirectAttributes.addFlashAttribute("success", "Usuário adicionado com sucesso!");
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        Optional<User> userOptional = userService.findById(id);
        if (!userOptional.isPresent()) { // Compatível com Java 8
            // Adicionar mensagem de erro ou redirecionar se o usuário não for encontrado
            return "redirect:/admin/users";
        }
        model.addAttribute("user", userOptional.get());
        return "edit-user";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User userForm,
                             @RequestParam(required = false) String password,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userService.findById(userForm.getId());

        if (!optionalUser.isPresent()) { // Compatível com Java 8
            redirectAttributes.addFlashAttribute("error", "Usuário não encontrado para atualização.");
            return "redirect:/admin/users";
        }

        User existingUser = optionalUser.get();

        // Verificar se o novo email já pertence a outro usuário (exceto o próprio usuário)
        Optional<User> userWithNewEmail = userService.findByEmail(userForm.getEmail());
        if (userWithNewEmail.isPresent() && !userWithNewEmail.get().getId().equals(existingUser.getId())) {
            model.addAttribute("error", "Este e-mail já está em uso por outro usuário.");
            model.addAttribute("user", userForm); // Retorna os dados do formulário para correção
            return "edit-user"; // Renderiza a página de edição novamente com o erro
        }

        // Não permitir alterar o email do admin@jekoo.com para outro se ele for o admin@jekoo.com
        // Ou não permitir que outro usuário mude seu email para admin@jekoo.com
        if ("admin@jekoo.com".equals(userForm.getEmail()) && !"admin@jekoo.com".equals(existingUser.getEmail())) {
            model.addAttribute("error", "O e-mail admin@jekoo.com é reservado.");
            model.addAttribute("user", userForm);
            return "edit-user";
        }
        // Impede que o email do admin@jekoo.com seja alterado
        if ("admin@jekoo.com".equals(existingUser.getEmail()) && !existingUser.getEmail().equals(userForm.getEmail())) {
            model.addAttribute("error", "O e-mail do administrador principal (admin@jekoo.com) não pode ser alterado.");
            model.addAttribute("user", userForm); // Mantém o email original no formulário
            userForm.setEmail(existingUser.getEmail()); // Restaura o email original no objeto que vai ser editado
            return "edit-user";
        }


        existingUser.setEmail(userForm.getEmail());
        existingUser.setRole(userForm.getRole());

        // Não permitir que o admin@jekoo.com seja rebaixado de ADMIN
        if ("admin@jekoo.com".equals(existingUser.getEmail()) && !"ADMIN".equals(userForm.getRole())) {
            model.addAttribute("error", "O administrador principal (admin@jekoo.com) não pode ter sua função alterada de ADMIN.");
            model.addAttribute("user", userForm);
            userForm.setRole("ADMIN"); // Restaura o role original
            return "edit-user";
        }


        if (password != null && !password.trim().isEmpty()) { // Compatível com Java 8
            existingUser.setPassword(password); // A senha será encriptada pelo UserService
        }

        userService.save(existingUser);
        redirectAttributes.addFlashAttribute("success", "Usuário atualizado com sucesso!");
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.findById(id);

        if (!userOptional.isPresent()) { // Compatível com Java 8
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