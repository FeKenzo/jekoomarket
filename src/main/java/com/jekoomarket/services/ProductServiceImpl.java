package com.jekoomarket.services;

import com.jekoomarket.models.Product;
import com.jekoomarket.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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
            // O JpaRepository.save() retorna a entidade salva, que geralmente já inclui o ID gerado.
            logger.info("Serviço: Produto salvo com ID: {}", savedProduct.getId());
        } catch (Exception e) {
            logger.error("Serviço: Erro ao tentar salvar produto no repositório. Título: {}. Erro: {}", product.getTitle(), e.getMessage(), e);
            // Relançar a exceção permite que o controller a capture ou que o Spring a manipule.
            // Ou você pode tratar de forma diferente aqui, se necessário.
            throw e;
        }
    }

    @Override
    public List<Product> findLatest(int count) {
        // Garante que count seja pelo menos 1 para evitar erros com PageRequest
        int itemsToFetch = Math.max(1, count);
        return productRepository.findAll(PageRequest.of(0, itemsToFetch, Sort.by(Sort.Direction.DESC, "id"))).getContent();
    }

    @Override
    public List<Product> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return productRepository.findAll(Sort.by(Sort.Direction.DESC, "id")); // Ou uma lista vazia: Collections.emptyList();
        }
        return productRepository.findByTitleContainingIgnoreCase(query);
    }

    @Override
    public List<Product> findByUser(Long userId) {
        logger.info("Serviço: Buscando produtos para o usuário ID: {}", userId);
        return productRepository.findByUserId(userId);
    }
}