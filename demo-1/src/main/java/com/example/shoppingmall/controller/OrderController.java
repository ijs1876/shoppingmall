package com.example.shoppingmall.controller;

import com.example.shoppingmall.entity.Order;
import com.example.shoppingmall.entity.Product;
import com.example.shoppingmall.repository.OrderRepository;
import com.example.shoppingmall.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/purchase")
    public ResponseEntity<Order> purchase(@RequestBody Order order, Authentication auth) { // 구매
        order.setUser((com.example.shoppingmall.entity.User) auth.getPrincipal());
        Product product = productRepository.findById(order.getProduct().getId()).orElseThrow(() -> new RuntimeException("Product not found with id: " + order.getProduct().getId()));
        order.setProduct(product);
        order.setOrderDate(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);
        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping
    public List<Order> getOrders(Authentication auth) { // 주문 히스토리
        Long userId = ((com.example.shoppingmall.entity.User) auth.getPrincipal()).getId();
        return orderRepository.findByUserId(userId);
    }
}