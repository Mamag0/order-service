package com.log430.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.log430.orderservice.dto.OrderSubmittedEvent;
import com.log430.orderservice.entity.Outbox;
import com.log430.orderservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxProcessor {

    private final OutboxRepository outboxRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 500)
    @Transactional
    public void processOutboxEvents() {
        List<Outbox> events = outboxRepository.findTop50BySentFalseOrderByCreatedAtAsc();

        for (Outbox event : events) {
            try {
                OrderSubmittedEvent orderEvent = objectMapper.readValue(event.getPayload(), OrderSubmittedEvent.class);

                orderEventPublisher.publishOrderSubmitted(orderEvent);

                event.setSent(true);
                outboxRepository.save(event);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Scheduled(cron = "0 0 * * * *") // toutes les heures
    @Transactional
    public void cleanupOutbox() {
        outboxRepository.deleteBySentTrueAndCreatedAtBefore(LocalDateTime.now().minusDays(1));
    }
}
