package com.log430.orderservice.service;

import com.log430.orderservice.entity.Order;
import com.log430.orderservice.repository.OrderRepository;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderCacheService {

    private final OrderRepository orderRepository;

    // =================== CACHES ===================
    @Cacheable(value = "buyMatchingOrders", key = "#symbol + ':' + #price") public List<Order> findBuyMatchingOrders(
            String symbol, Double price) {
        return orderRepository.findBuyMatchingOrders(symbol, price);
    }

    @Cacheable(value = "sellMatchingOrders", key = "#symbol + ':' + #price")
    public List<Order> findSellMatchingOrders(String symbol, Double price) {
        return orderRepository.findSellMatchingOrders(symbol, price);
    }

    @Cacheable(value = "orderByIdempotency", key = "#idempotencyKey")
    public Optional<Order> findByIdempotencyKey(String idempotencyKey) {
        return orderRepository.findByIdempotencyKey(idempotencyKey);
    }

    // =================== SAVE + INVALIDATION ===================
    @Caching(evict = {@CacheEvict(value = "buyMatchingOrders", key = "#order.symbol + ':' + #order.price", condition = "#order.side == 'SELL'" // un SELL impacte les acheteurs
    ), @CacheEvict(value = "sellMatchingOrders", key = "#order.symbol + ':' + #order.price", condition = "#order.side == 'BUY'" // un BUY impacte les vendeurs
    )}) public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}
