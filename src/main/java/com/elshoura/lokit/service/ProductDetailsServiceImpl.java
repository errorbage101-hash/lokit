package com.elshoura.lokit.service;

import com.elshoura.lokit.errors.exception.NotFoundException;
import com.elshoura.lokit.models.dto.response.ProductDetailsResponse;
import com.elshoura.lokit.models.dto.response.ProductImageResponse;
import com.elshoura.lokit.models.dto.response.ProductVariantDetailsResponse;
import com.elshoura.lokit.models.entitys.Product;
import com.elshoura.lokit.models.entitys.ProductImage;
import com.elshoura.lokit.models.entitys.ProductVariant;
import com.elshoura.lokit.repository.ProductImageRepository;
import com.elshoura.lokit.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductDetailsServiceImpl implements ProductDetailsService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    @Transactional(readOnly = true)
    public ProductDetailsResponse getProductDetails(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        List<ProductImageResponse> images = productImageRepository.findByProductIdOrderByIdAsc(productId)
                .stream()
                .map(this::toImageResponse)
                .toList();

        List<ProductVariantDetailsResponse> variants = product.getVariants()
                .stream()
                .map(this::toVariantResponse)
                .toList();

        return ProductDetailsResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())

                .brandId(product.getBrand().getId())
                .brandName(product.getBrand().getName())

                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())

                .departmentId(product.getDepartment().getId())
                .departmentName(product.getDepartment().getName())

                .materialId(product.getMaterial().getId())
                .materialName(product.getMaterial().getName())

                .active(product.getActive())

                .images(images)
                .variants(variants)

                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private ProductImageResponse toImageResponse(ProductImage image) {
        return ProductImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .isMain(image.getIsMain())
                .build();
    }

    private ProductVariantDetailsResponse toVariantResponse(ProductVariant variant) {
        return ProductVariantDetailsResponse.builder()
                .id(variant.getId())

                .sizeId(variant.getSize().getId())
                .sizeName(variant.getSize().getName())

                .colorId(variant.getColor().getId())
                .colorName(variant.getColor().getName())

                .sku(variant.getSku())
                .price(variant.getPrice())
                .stock(variant.getStock())
                .build();
    }
}
