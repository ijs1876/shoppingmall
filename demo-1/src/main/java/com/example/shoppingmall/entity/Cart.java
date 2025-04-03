package com.example.shoppingmall.entity;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;       // 장바구니 소유자
    @ManyToOne
    private Product product; // 상품
    private int quantity;    // 수량
}