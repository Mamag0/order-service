package com.log430.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {
    private String orderId;
    private String status;
    private String symbole;
    private String side;
    private String reason;
    private Instant timestamp;

    public static OrderResponseDto reject(String reason) {
        return OrderResponseDto.builder().status("REJECT").reason(reason).timestamp(Instant.now()).build();
    }

    // Méthode utilitaire pour succès
    public static OrderResponseDto success(String orderId, String symbole, String side) {
        return OrderResponseDto.builder().orderId(orderId).status("ACCEPTED").symbole(symbole).side(side).timestamp(
                Instant.now()).build();
    }
}
