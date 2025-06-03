package com.jekoomarket.services;

import com.jekoomarket.models.Product;
import com.jekoomarket.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void save(Product product) {
        productRepository.save(product);
    }

    @Override
    public List<Product> findLatest(int count) {
        return productRepository.findAll(PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "id"))).getContent();
    }

    @Override
    public List<Product> search(String query) {
        return productRepository.findByTitleContainingIgnoreCase(query);
    }
}