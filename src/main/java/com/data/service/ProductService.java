package com.data.service;

import com.data.dto.ProductDTO;
import com.data.entity.Product;
import com.data.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProducts(int page, int size) {
        return productRepository.findAll(page, size);
    }

    public long getTotalProducts() {
        return productRepository.countTotalProducts();
    }

    public List<Product> searchProducts(String search, BigDecimal minPrice, BigDecimal maxPrice, Integer stock, int page, int size) {
        return productRepository.searchProducts(search, minPrice, maxPrice, stock, page, size);
    }

    public long countProducts(String search, BigDecimal minPrice, BigDecimal maxPrice, Integer stock) {
        return productRepository.countProducts(search, minPrice, maxPrice, stock);
    }

    public boolean addProduct(Product product) {
        return productRepository.save(product);
    }

    public boolean deleteProduct(Integer id) {
        return productRepository.deleteById(id);
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id);
    }

    public boolean updateProduct(Product product) {
        return productRepository.update(product);
    }

    public boolean isNameDuplicate(String name, Integer id) {
        return productRepository.existsByNameAndIdNot(name, id);
    }

    public boolean isNameDuplicate(String name) {
        return productRepository.existsByName(name);
    }
}