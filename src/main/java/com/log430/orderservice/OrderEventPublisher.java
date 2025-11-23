package com.log430.orderservice;

import com.log430.orderservice.dto.OrderSubmittedEvent;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;

@Service
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderSubmitted(OrderSubmittedEvent event) {
        kafkaTemplate.send("orders.submitted", event);
        log.debug("OrderSubmittedEvent publié pour orderId={}", event.getOrderId());
    }
}

