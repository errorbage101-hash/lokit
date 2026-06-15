package com.elshoura.lokit.repository;

import com.elshoura.lokit.models.entitys.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {
            "user",
            "address"
    })
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Override
    @EntityGraph(attributePaths = {
            "user",
            "address"
    })
    Optional<Order> findById(Long id);
}