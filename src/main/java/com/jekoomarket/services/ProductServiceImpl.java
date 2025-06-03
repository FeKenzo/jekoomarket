package com.jekoomarket.services;

import com.jekoomarket.models.Product;
import com.jekoomarket.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional; // Adicionado para findById

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void save(Product product) {
        logger.info("Serviço: Tentando salvar produto com título: {}", product.getTitle());
        try {
            Product savedProduct = productRepository.save(product);
            logger.info("Serviço: Produto salvo com ID: {}", savedProduct.getId());
        } catch (Exception e) {
            logger.error("Serviço: Erro ao tentar salvar produto no repositório. Título: {}. Erro: {}", product.getTitle(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Product> findLatest(int count) {
        int itemsToFetch = Math.max(1, count);
        return productRepository.findAll(PageRequest.of(0, itemsToFetch, Sort.by(Sort.Direction.DESC, "id"))).getContent();
    }

    @Override
    public List<Product> search(String query) {
        if (query == null || query.trim().isEmpty()) { // Java 8 compatible
            return Collections.emptyList();
        }
        return productRepository.findByTitleContainingIgnoreCase(query);
    }

    @Override
    public List<Product> findByUser(Long userId) {
        logger.info("Serviço: Buscando produtos para o usuário ID: {}", userId);
        return productRepository.findByUserId(userId);
    }

    @Override
    public Optional<Product> findById(Long id) { // Adicionado
        logger.info("Serviço: Buscando produto com ID: {}", id);
        return productRepository.findById(id);
    }
}