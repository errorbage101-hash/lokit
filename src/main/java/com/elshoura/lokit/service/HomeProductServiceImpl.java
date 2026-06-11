package com.elshoura.lokit.service;

import com.elshoura.lokit.models.dto.response.ProductSearchResponse;
import com.elshoura.lokit.models.entitys.Product;
import com.elshoura.lokit.repository.ProductImageRepository;
import com.elshoura.lokit.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeProductServiceImpl implements HomeProductService {



    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    @Transactional(readOnly = true)

    public List<ProductSearchResponse> getNewArrivals() {
        return productRepository.findTop8ByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::toProductCardResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)

    public List<ProductSearchResponse> getLatestProducts() {
        return productRepository.findTop12ByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::toProductCardResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSearchResponse> getProductsByDepartment(Long departmentId) {
        return productRepository.findByDepartmentIdAndActiveTrueOrderByCreatedAtDesc(departmentId)
                .stream()
                .map(this::toProductCardResponse)
                .toList();
    }

    private ProductSearchResponse toProductCardResponse(Product product) {

        String imageUrl = productImageRepository.findFirstByProductIdAndIsMainTrue(product.getId())
                .or(() -> productImageRepository.findFirstByProductIdOrderByIdAsc(product.getId()))
                .map(productImage -> productImage.getImageUrl())
                .orElse(null);

        BigDecimal minPrice = product.getVariants()
                .stream()
                .map(variant -> variant.getPrice())
                .min(Comparator.naturalOrder())
                .filter(price -> price != null)
                .orElse(BigDecimal.ZERO);

        return ProductSearchResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .brandName(product.getBrand().getName())
                .categoryName(product.getCategory().getName())
                .imageUrl(imageUrl)
                .minPrice(minPrice)
                .build();
    }
}
