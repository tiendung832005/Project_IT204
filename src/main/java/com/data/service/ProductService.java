package com.data.service;

import com.data.entity.Product;
import com.data.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Product> searchProductsByBrand(String brand, int page, int size) {
        return productRepository.searchByBrand(brand, page, size);
    }

    public long getTotalProductsByBrand(String brand) {
        return productRepository.countProductsByBrand(brand);
    }

    public boolean addProduct(Product product) {
        return productRepository.save(product);
    }

    public boolean deleteProduct(Integer id) {
        return productRepository.deleteById(id);
    }
}