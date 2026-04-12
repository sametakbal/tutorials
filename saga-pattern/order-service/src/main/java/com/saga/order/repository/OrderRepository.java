package com.saga.order.repository;

import com.saga.order.model.Order;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// Gerçek uygulamada JPA/MongoDB repository kullanılır
@Repository
public class OrderRepository {

    private final Map<String, Order> store = new ConcurrentHashMap<>();

    public void save(Order order) {
        store.put(order.getId(), order);
    }

    public Optional<Order> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Collection<Order> findAll() {
        return store.values();
    }
}
