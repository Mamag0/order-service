package com.log430.orderservice.repository;

import com.log430.orderservice.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    List<Outbox> findTop50BySentFalseOrderByCreatedAtAsc();

    void deleteBySentTrueAndCreatedAtBefore(LocalDateTime date);
}

