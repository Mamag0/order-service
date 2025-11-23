package com.log430.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "outbox")
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID de l'objet lié (par exemple orderId)
    @Column(nullable = false)
    private String aggregateId;

    // Type d'événement (ORDER_SUBMITTED, ORDER_MATCHED, TRADE_CREATED…)
    @Column(nullable = false)
    private String type;

    // JSON de l'événement
    @Lob
    @Column(nullable = false)
    private String payload;

    @Column(nullable = false)
    private Boolean sent = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Outbox (String aggregateId, String type, String payload){

        this.aggregateId = aggregateId;
        this.type = type;
        this.payload = payload;
    }

}

