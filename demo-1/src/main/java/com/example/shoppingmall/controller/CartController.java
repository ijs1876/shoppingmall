package com.example.shoppingmall.controller;

import com.example.shoppingmall.entity.Cart;
import com.example.shoppingmall.entity.Order;
import com.example.shoppingmall.entity.Product;
import com.example.shoppingmall.repository.CartRepository;
import com.example.shoppingmall.repository.OrderRepository;
import com.example.shoppingmall.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody Cart cart, Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        cart.setUser((com.example.shoppingmall.entity.User) auth.getPrincipal());
        Product product = productRepository.findById(cart.getProduct().getId())
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + cart.getProduct().getId()));
        cart.setProduct(product);
        Cart savedCart = cartRepository.save(cart);
        return ResponseEntity.ok(savedCart);
    }

    @GetMapping
    public List<Cart> getCart(Authentication auth) {
        Long userId = ((com.example.shoppingmall.entity.User) auth.getPrincipal()).getId();
        return cartRepository.findByUserId(userId);
    }

    // 장바구니에서 구매 후 삭제
    @PostMapping("/purchase")
    public ResponseEntity<String> purchaseFromCart(@RequestBody List<Long> cartIds, Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        com.example.shoppingmall.entity.User user = (com.example.shoppingmall.entity.User) auth.getPrincipal();

        for (Long cartId : cartIds) {
            Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartId));
            
            // 주문 생성
            Order order = new Order();
            order.setUser(user);
            order.setProduct(cart.getProduct());
            order.setQuantity(cart.getQuantity());
            order.setOrderDate(LocalDateTime.now());
            orderRepository.save(order);

            // 장바구니에서 삭제
            cartRepository.deleteById(cartId);
        }
        return ResponseEntity.ok("Purchase completed and items removed from cart");
    }
}