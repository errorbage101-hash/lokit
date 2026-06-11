package com.elshoura.lokit.repository;

import com.elshoura.lokit.models.entitys.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> , JpaSpecificationExecutor<Product> {

    @EntityGraph(attributePaths = {"variants", "brand", "category"})
    List<Product> findByBrandIdOrderByIdAsc(Long brandId);

    @EntityGraph(attributePaths = {"variants", "brand", "category"})
    List<Product> findByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = {"variants", "brand", "category"})
    List<Product> findByDepartmentId(Long departmentId);

    @EntityGraph(attributePaths = {"variants", "brand", "category"})
    Optional<Product> findProductById(Long id);

    @EntityGraph(attributePaths = {"variants", "brand", "category"})
    List<Product> findTop8ByActiveTrueOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"variants", "brand", "category"})
    List<Product> findByDepartmentIdAndActiveTrueOrderByCreatedAtDesc(Long departmentId);

    @EntityGraph(attributePaths = {"variants", "brand", "category"})
    List<Product> findTop12ByActiveTrueOrderByCreatedAtDesc();
 }
