package com.log430.orderservice.consummer;


import com.log430.orderservice.dto.OrderUpdatedEvent;
import com.log430.orderservice.entity.Order;
import com.log430.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "orders.updated", groupId = "order-service")
    public void onOrderUpdated(OrderUpdatedEvent evt) {
        log.info("Trade Recu {}",evt);
        orderService.updateOrder(evt);
    }
}

