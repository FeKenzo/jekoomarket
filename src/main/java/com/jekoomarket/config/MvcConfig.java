package com.jekoomarket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Nome da pasta onde as imagens são salvas (na raiz do projeto)
        String uploadDirName = "product-images";
        Path uploadDir = Paths.get(uploadDirName);
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        // Mapeia a URL /product-images/** para a pasta física no seu computador
        registry.addResourceHandler("/" + uploadDirName + "/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}