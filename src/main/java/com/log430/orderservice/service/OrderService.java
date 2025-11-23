package com.log430.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.log430.orderservice.dto.OrderUpdatedEvent;
import com.log430.orderservice.entity.Order;
import com.log430.orderservice.OrderEventPublisher;
import com.log430.orderservice.dto.OrderRequestDto;
import com.log430.orderservice.dto.OrderResponseDto;
import com.log430.orderservice.dto.OrderSubmittedEvent;
import com.log430.orderservice.entity.Outbox;
import com.log430.orderservice.repository.OrderRepository;
import com.log430.orderservice.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void updateOrder(OrderUpdatedEvent order) {
        Order oldOrder = orderRepository.findById(order.getOrderId()).orElseThrow();
        oldOrder.setStatus(order.getStatus());
        oldOrder.setQuantity(order.getRemainingQty());
    }

    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto request, String idempotencyKey) {


//        Optional<Order> existing = orderRepository.findByIdempotencyKey(idempotencyKey);//Could be opti
//        if (existing.isPresent()) {
//            Order order = existing.get();
//            return new OrderResponseDto(order.getId().toString(), order.getStatus(), null, order.getCreatedAt());
//        }

        // ---------- Pré-trade checks ----------
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            return OrderResponseDto.reject("Quantity must be > 0");
        }

        if (request.getSymbol() == null || request.getSymbol().isEmpty()) {
            return OrderResponseDto.reject("Symbol is required");
        }

        if ("LIMIT".equalsIgnoreCase(request.getType()) && (request.getPrice() == null || request.getPrice() <= 0)) {
            return OrderResponseDto.reject("Price required for LIMIT order");
        }


        // TODO: ajouter checks fonds, restrictions short-sell, tick size, etc.

        // ---------- Normalisation / horodatage ----------
        Instant now = Instant.now();
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setSymbol(request.getSymbol());
        order.setSide(request.getSide());
        order.setType(request.getType());
        order.setQuantity(request.getQuantity());
        order.setPrice(request.getPrice());
        order.setTimeInForce(request.getTimeInForce());
        order.setStatus("ACK");
        order.setCreatedAt(now);

        // ---------- Persistance ----------
        Order savedOrder = orderRepository.save(order);

        //Publish
        OrderSubmittedEvent event = new OrderSubmittedEvent(order.getId(),
                                                            order.getUserId(),
                                                            order.getSymbol(),
                                                            order.getSide(),
                                                            order.getPrice(),
                                                            order.getQuantity(),
                                                            order.getCreatedAt(),
                                                            order.getTimeInForce(),
                                                            order.getStatus());

        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize OrderSubmittedEvent", e);
        }

        //outbox
        outboxRepository.save(new Outbox(savedOrder.getId().toString(),
                                         "OrderCreated",
                                         payload));
        //matchingEngineService.processNewOrder(order);

        return new OrderResponseDto(savedOrder.getId().toString(), savedOrder.getStatus(), savedOrder.getSymbol(),
                                    savedOrder.getType(), null, now);
    }

    public Page<Order> getOrders(List<String> statuses, String symbol, String side, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orders = orderRepository.findAllByFilters(statuses, symbol, side, pageable);

        return orders;
    }

}
