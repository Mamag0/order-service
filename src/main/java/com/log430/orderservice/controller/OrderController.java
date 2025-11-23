package com.log430.orderservice.controller;

import com.log430.orderservice.service.OrderService;
import com.log430.orderservice.dto.OrderRequestDto;
import com.log430.orderservice.dto.OrderResponseDto;
import com.log430.orderservice.service.OrderCacheService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderCacheService orderCacheService;
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @PostMapping public ResponseEntity<OrderResponseDto> placeOrder(@RequestBody @Valid OrderRequestDto request,
                                                                    @RequestHeader("X-User-Id") Long userId,
                                                                    @RequestHeader("X-User-Email") String userEmail,
                                                                    @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        request.setUserId(userId);

        log.info("Placing order for user={}, id={}", userEmail, userId);

        OrderResponseDto response = orderService.placeOrder(request, idempotencyKey);
        if ("REJECT".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping public ResponseEntity<?> getAllOrders(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(required = false) String symbol,
                                                      @RequestParam(required = false) String side,
                                                      @RequestParam List<String> statuses) {
        log.info("Fetching orders for, id={}, page={}, size={}, symbol={}, side={}", statuses, page, size, symbol,
                 side);

        return ResponseEntity.ok(orderService.getOrders(statuses, symbol, side, page, size));
    }
}
