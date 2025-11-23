package com.log430.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "orders", indexes = {@Index(name = "idx_symbol_side_status_price_createdAt", columnList = "symbol, side, status, price, createdAt"), @Index(name = "idx_idempotency_key", columnList = "idempotencyKey")})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String symbol;
    private String side;
    private String type;
    private Double quantity;
    private Double price;
    private String timeInForce;
    private String status;
    private Instant createdAt;
    private String idempotencyKey;
}
