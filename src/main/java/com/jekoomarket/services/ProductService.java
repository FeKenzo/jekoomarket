package com.jekoomarket.services;

import com.jekoomarket.models.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    void save(Product product);
    List<Product> findLatest(int count);
    List<Product> search(String query);
    List<Product> findByUser(Long userId);
    Optional<Product> findById(Long id);
}