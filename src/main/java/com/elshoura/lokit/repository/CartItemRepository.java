package com.elshoura.lokit.repository;

import com.elshoura.lokit.models.entitys.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @EntityGraph(attributePaths = {
            "variant",
            "variant.product",
            "variant.size",
            "variant.color"
    })
    List<CartItem> findByCartId(Long id);

    @EntityGraph(attributePaths = {
            "cart",
            "variant",
            "variant.product",
            "variant.size",
            "variant.color"
    })
    Optional<CartItem> findByCart_IdAndVariant_Id(Long id, Long variantId);

    @Override
    @EntityGraph(attributePaths = {
            "cart",
            "variant",
            "variant.product",
            "variant.size",
            "variant.color"
    })
    Optional<CartItem> findById(Long id);
}
