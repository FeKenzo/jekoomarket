package com.jekoomarket.config;

import com.jekoomarket.models.User;
import com.jekoomarket.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@jekoo.com").isEmpty()) {
                User admin = new User();
                admin.setEmail("admin@jekoo.com");
                admin.setPassword(passwordEncoder.encode("admin123")); // senha segura
                admin.setRole("ADMIN"); // precisa ser compatível com seu filtro hasRole("ADMIN")

                userRepository.save(admin);
                System.out.println("Usuário admin criado com sucesso!");
            }
        };
    }
}
