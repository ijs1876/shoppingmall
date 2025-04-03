package com.example.shoppingmall.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.shoppingmall.entity.Product;
import com.example.shoppingmall.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return ResponseEntity.ok(product);
    }

    @GetMapping("/products/search")
    public List<Product> searchProducts(@RequestParam("name") String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/admin/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> addProduct(
            @RequestParam("name") String name,
            @RequestParam("price") double price,
            @RequestParam("stock") int stock,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);

        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            File uploadDirFile = new File(uploadDir);
            System.out.println("Upload directory: " + uploadDir); // 디버깅
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            File uploadFile = new File(uploadDirFile, fileName);
            image.transferTo(uploadFile);
            product.setImageUrl("/images/" + fileName); // URL은 프론트엔드 기준 유지
        }

        Product savedProduct = productRepository.save(product);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());
        existing.setImageUrl(product.getImageUrl());
        Product updatedProduct = productRepository.save(existing);
        return ResponseEntity.ok(updatedProduct);
    }
}