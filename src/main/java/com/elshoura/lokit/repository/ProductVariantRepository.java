package com.elshoura.lokit.repository;

import com.elshoura.lokit.models.entitys.ProductVariant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant,Long> {

    @Override
    @EntityGraph(attributePaths = {
            "product",
            "size",
            "color"
    })
    Optional<ProductVariant> findById(Long id);

    @EntityGraph(attributePaths = {
            "product",
            "size",
            "color"
    })
    List<ProductVariant> findByProductId(Long productId);

    @Override
    @EntityGraph(attributePaths = {
            "product",
            "size",
            "color"
    })
    List<ProductVariant> findAll();

    boolean existsByProductIdAndSizeIdAndColorId(Long productId, Long sizeId, Long colorId);

    boolean existsBySku(String sku);

}
