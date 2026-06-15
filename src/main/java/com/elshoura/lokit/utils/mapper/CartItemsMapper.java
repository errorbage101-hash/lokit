package com.elshoura.lokit.utils.mapper;

import com.elshoura.lokit.models.dto.response.CartItemResponse;
import com.elshoura.lokit.models.entitys.CartItem;
import com.elshoura.lokit.models.entitys.ProductVariant;

import java.math.BigDecimal;

public class CartItemsMapper {

    private CartItemsMapper() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    public static CartItemResponse mapCartItem(CartItem item) {

        ProductVariant variant = item.getVariant();

        BigDecimal unitPrice = variant.getPrice();
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .id(item.getId())
                .variantId(variant.getId())
                .productName(variant.getProduct().getName())
                .sizeName(variant.getSize().getName())
                .colorName(variant.getColor().getName())
                .quantity(item.getQuantity())
                .unitPrice(unitPrice)
                .lineTotal(lineTotal)
                .build();
    }
}