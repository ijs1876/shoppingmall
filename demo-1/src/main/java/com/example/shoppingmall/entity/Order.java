package com.example.shoppingmall.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;          // 주문자
    @ManyToOne
    private Product product;    // 주문 상품
    private int quantity;       // 주문 수량
    private LocalDateTime orderDate; // 주문 날짜
}