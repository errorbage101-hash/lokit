package com.elshoura.lokit.service;

import com.elshoura.lokit.errors.exception.NotFoundException;
import org.springframework.transaction.annotation.Transactional;
import com.elshoura.lokit.errors.exception.QuantityExceedsException;
import com.elshoura.lokit.errors.exception.UserForbiddenException;
import com.elshoura.lokit.models.dto.request.CartItemRequest;
import com.elshoura.lokit.models.dto.request.UpdateCartItemRequest;
import com.elshoura.lokit.models.dto.response.CartItemResponse;
import com.elshoura.lokit.models.dto.response.CartResponse;
import com.elshoura.lokit.models.entitys.Cart;
import com.elshoura.lokit.models.entitys.CartItem;
import com.elshoura.lokit.models.entitys.ProductVariant;
import com.elshoura.lokit.repository.CartItemRepository;
import com.elshoura.lokit.repository.ProductVariantRepository;
import com.elshoura.lokit.utils.mapper.CartItemsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getMyCart(Long userId){

        Cart cart = cartService.getOrCreateCart(userId);

        return mapCart(cart);
    }

    @Override
    @Transactional
    public CartResponse addItem(Long userId, CartItemRequest cartItemRequest){

    Cart cart = cartService.getOrCreateCart(userId);

        ProductVariant productVariant = productVariantRepository.findById(cartItemRequest.variantId())
                .orElseThrow(()->new NotFoundException("product variant not found"));

        CartItem item = cartItemRepository.findByCart_IdAndVariant_Id(cart.getId(),cartItemRequest.variantId())
                .orElse(null);

        if(item == null){
            item = CartItem.builder()
                    .cart(cart)
                    .variant(productVariant)
                    .quantity(cartItemRequest.quantity())
                    .build();
        }
        else {

            item.setQuantity(item.getQuantity() + cartItemRequest.quantity());

        }

     if( item.getQuantity() > productVariant.getStock()){

      throw new QuantityExceedsException("Quantity exceeds available stock");

}
        cartItemRepository.save(item);
        return mapCart(cart);

    }
    @Override
    @Transactional
   public CartResponse updateItem(Long userId,Long itemId, UpdateCartItemRequest updateCartItemRequest){

 Cart cart = cartService.getOrCreateCart(userId);

 CartItem item = cartItemRepository.findById(itemId)
         .orElseThrow(()->new NotFoundException("Cart item not found"));

if(!item.getCart().getId().equals(cart.getId())) {
    throw new UserForbiddenException("Cart item does not belong to this user");
}
    if (updateCartItemRequest.quantity() > item.getVariant().getStock()) {
        throw new QuantityExceedsException("Quantity exceeds available stock");
    }

    item.setQuantity(updateCartItemRequest.quantity());
    cartItemRepository.save(item);
    return mapCart(cart);
   }

    @Override
    @Transactional
    public CartResponse removeItem(Long userId, Long itemId) {
        Cart cart = cartService.getOrCreateCart(userId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new UserForbiddenException("Cart item does not belong to this user");
        }

        cartItemRepository.delete(item);
        return mapCart(cart);
    }


    private CartResponse mapCart(Cart cart){

        List<CartItemResponse> items = cartItemRepository.findByCartId(cart.getId())
                .stream()
                .map(CartItemsMapper::mapCartItem)
                .toList();

        BigDecimal total =items.stream()
                .map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(items)
                .total(total)
                .build();

    }


}
