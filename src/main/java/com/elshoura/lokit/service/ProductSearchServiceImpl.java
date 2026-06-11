package com.elshoura.lokit.service;


import com.elshoura.lokit.models.dto.response.ProductSearchResponse;
import com.elshoura.lokit.models.entitys.Product;
import com.elshoura.lokit.models.entitys.ProductSpecification;
import com.elshoura.lokit.repository.ProductImageRepository;
import com.elshoura.lokit.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductSearchResponse> searchProducts(
            String keyword,
            Long brandId,
            Long categoryId,
            Long colorId,
            Long sizeId,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        return productRepository.findAll(
                        ProductSpecification.search(
                                keyword,
                                brandId,
                                categoryId,
                                colorId,
                                sizeId,
                                minPrice,
                                maxPrice
                        )
                )
                .stream()
                .map(this::toProductSearchResponse)
                .toList();
    }

    private ProductSearchResponse toProductSearchResponse(Product product) {

        String imageUrl = productImageRepository.findFirstByProductIdAndIsMainTrue(product.getId())
                .or(() -> productImageRepository.findFirstByProductIdOrderByIdAsc(product.getId()))
                .map(productImage -> productImage.getImageUrl())
                .orElse(null);

        BigDecimal minPrice = product.getVariants()
                .stream()
                .map(variant -> variant.getPrice())
                .min(Comparator.naturalOrder())
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
