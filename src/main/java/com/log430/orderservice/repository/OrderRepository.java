package com.log430.orderservice.repository;

import com.log430.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
                SELECT o
                FROM Order o
                WHERE o.symbol = :symbol
                  AND o.side = 'SELL'                 
                  AND o.status IN ('ACK','WORKING','PARTIALLY_FILLED')
                  AND o.price <= :price          
                ORDER BY o.price DESC, o.createdAt ASC
            """) List<Order> findBuyMatchingOrders(@Param("symbol") String symbol, @Param("price") Double price);


    @Query("""
                SELECT o 
                FROM Order o 
                WHERE o.symbol = :symbol 
                  AND o.side = 'BUY'
                  AND o.status IN ('ACK','WORKING','PARTIALLY_FILLED') 
                  AND o.price >= :price 
                ORDER BY o.price ASC, o.createdAt ASC
            """) List<Order> findSellMatchingOrders(@Param("symbol") String symbol, @Param("price") Double price);


    @Query("""
                SELECT o FROM Order o
                WHERE o.status IN :statuses
                  AND (:symbol IS NULL OR o.symbol = :symbol)
                  AND (:side IS NULL OR o.side = :side)
                ORDER BY o.createdAt DESC
            """) Page<Order> findAllByFilters(@Param("statuses") List<String> statuses, @Param("symbol") String symbol,
                                              @Param("side") String side, Pageable pageable);


    Optional<Order> findByIdempotencyKey(String idempotencyKey);
}

