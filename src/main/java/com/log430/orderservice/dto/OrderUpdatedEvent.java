package com.log430.orderservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class OrderUpdatedEvent {
    private Long orderId;
    private String status;
    private double remainingQty;

}

