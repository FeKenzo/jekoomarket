package com.jekoomarket.repositories;

import com.jekoomarket.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Encontra produtos cujo título contém a string de busca, ignorando maiúsculas e minúsculas.
     * Usado para a funcionalidade da barra de pesquisa.
     * @param title A string de texto para buscar no título dos produtos.
     * @return Uma lista de produtos que correspondem ao critério de busca.
     */
    List<Product> findByTitleContainingIgnoreCase(String title);
}