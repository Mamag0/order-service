package com.log430.orderservice.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSubmittedEvent {
    private Long orderId, userId;
    private String symbol, side;
    private double price, quantity;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;
    private String timeInForce;
    private String status;

}

