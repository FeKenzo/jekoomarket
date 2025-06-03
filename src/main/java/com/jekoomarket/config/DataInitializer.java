package com.jekoomarket.config;

import com.jekoomarket.models.Product;
import com.jekoomarket.models.User;
import com.jekoomarket.repositories.ProductRepository;
import com.jekoomarket.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      ProductRepository productRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            logger.info("Iniciando a inicialização de dados...");
            try {
                User adminUser;
                Optional<User> existingAdminOptional = userRepository.findByEmail("admin@jekoo.com");

                if (!existingAdminOptional.isPresent()) { // Java 8 compatible
                    User admin = new User();
                    admin.setEmail("admin@jekoo.com");
                    admin.setPassword(passwordEncoder.encode("admin123"));
                    admin.setRole("ADMIN");
                    admin.setCep("01001-000");
                    admin.setLogradouro("Praça da Sé");
                    admin.setNumero("100");
                    admin.setComplemento("Lado A");
                    admin.setBairro("Sé");
                    admin.setCidade("São Paulo");
                    admin.setEstado("SP");
                    adminUser = userRepository.save(admin);
                    logger.info("Usuário admin (admin@jekoo.com) criado com sucesso!");
                } else {
                    adminUser = existingAdminOptional.get();
                    logger.info("Usuário admin (admin@jekoo.com) já existe.");
                    if (adminUser.getCep() == null || adminUser.getCep().trim().isEmpty()) { // Java 8 compatible
                        adminUser.setCep("01001-000");
                        adminUser.setLogradouro("Praça da Sé");
                        adminUser.setNumero("100");
                        adminUser.setComplemento("Lado A");
                        adminUser.setBairro("Sé");
                        adminUser.setCidade("São Paulo");
                        adminUser.setEstado("SP");
                        userRepository.save(adminUser);
                        logger.info("Endereço do usuário admin (admin@jekoo.com) atualizado.");
                    }
                }

                if (productRepository.count() == 0 && adminUser != null) {
                    logger.info("Criando produtos de exemplo...");

                    Product p1 = new Product();
                    p1.setTitle("Vaso Ecológico de Garrafa PET");
                    p1.setDescription("Vaso para plantas feito artesanalmente com garrafas PET recicladas.");
                    p1.setPrice(15.50);
                    p1.setImageUrl("/product-images/sample-vaso.jpg");
                    p1.setUser(adminUser);

                    Product p2 = new Product();
                    p2.setTitle("Ecobag Sustentável de Algodão Cru");
                    p2.setDescription("Sacola reutilizável feita de algodão cru orgânico, perfeita para suas compras.");
                    p2.setPrice(29.90);
                    p2.setImageUrl("/product-images/sample-ecobag.jpg");
                    p2.setUser(adminUser);

                    Product p3 = new Product();
                    p3.setTitle("Composteira Doméstica Compacta");
                    p3.setDescription("Sistema de compostagem para resíduos orgânicos. Reduza seu lixo e produza adubo.");
                    p3.setPrice(125.00);
                    p3.setImageUrl("/product-images/sample-composteira.jpg");
                    p3.setUser(adminUser);

                    productRepository.saveAll(Arrays.asList(p1, p2, p3)); // Java 8 compatible
                    logger.info("Produtos de exemplo criados com sucesso!");
                } else if (adminUser == null) {
                    logger.warn("Usuário admin não pôde ser criado ou encontrado. Produtos de exemplo não foram criados.");
                } else {
                    logger.info("Produtos de exemplo já existem ou não foram criados.");
                }

            } catch (Exception e) {
                logger.error("Ocorreu um erro crítico durante a inicialização dos dados: {}", e.getMessage(), e);
            }
            logger.info("Finalizada a inicialização de dados.");
        };
    }
}