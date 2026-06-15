package com.elshoura.lokit.service;

import com.elshoura.lokit.errors.exception.AlreadyExistException;
import com.elshoura.lokit.errors.exception.NotFoundException;
import com.elshoura.lokit.models.dto.request.WishlistRequest;
import com.elshoura.lokit.models.dto.response.WishlistResponse;
import com.elshoura.lokit.models.entitys.Product;
import com.elshoura.lokit.models.entitys.User;
import com.elshoura.lokit.models.entitys.WishlistItem;
import com.elshoura.lokit.repository.ProductRepository;
import com.elshoura.lokit.repository.UserRepository;
import com.elshoura.lokit.repository.WishlistRepository;
import com.elshoura.lokit.utils.mapper.WishlistMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.elshoura.lokit.utils.mapper.WishlistMapper.toWishlist;
import static com.elshoura.lokit.utils.mapper.WishlistMapper.toWishlistItemResponse;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public WishlistResponse addWishlistItem(Long userId, WishlistRequest request){

        if(wishlistRepository.existsByUserIdAndProductId(userId, request.productId())){
            throw new AlreadyExistException("Product already in wishlist");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Product product = productRepository.findProductById(request.productId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        WishlistItem wishlistItem = toWishlist(user, product);

        return toWishlistItemResponse(wishlistRepository.save(wishlistItem));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistResponse> getMyWishlist(Long userId){
        return wishlistRepository.findByUserId(userId)
                .stream()
                .map(WishlistMapper::toWishlistItemResponse)
                .toList();

    }

    @Override
    public void remove(Long userId, Long productId){

        WishlistItem item = wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new NotFoundException("Wishlist item not found"));

        wishlistRepository.delete(item);

    }

}
