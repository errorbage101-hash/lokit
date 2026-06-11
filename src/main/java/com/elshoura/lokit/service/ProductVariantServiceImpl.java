package com.elshoura.lokit.service;

import com.elshoura.lokit.errors.exception.AlreadyExistException;
import com.elshoura.lokit.errors.exception.NotFoundException;
import com.elshoura.lokit.models.dto.request.UpdateStockRequest;
import com.elshoura.lokit.models.entitys.ProductVariant;
import com.elshoura.lokit.models.dto.request.ProductVariantRequest;
import com.elshoura.lokit.models.dto.response.ProductVariantResponse;
import com.elshoura.lokit.models.entitys.Color;
import com.elshoura.lokit.models.entitys.Product;
import com.elshoura.lokit.models.entitys.Size;
import com.elshoura.lokit.repository.ColorRepository;
import com.elshoura.lokit.repository.ProductRepository;
import com.elshoura.lokit.repository.ProductVariantRepository;
import com.elshoura.lokit.repository.SizeRepository;
import com.elshoura.lokit.utils.mapper.ProductVariantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.elshoura.lokit.utils.mapper.ProductVariantMapper.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final SizeRepository sizeRepository;
    private final ColorRepository colorRepository;

    @Override
    @Transactional
    public ProductVariantResponse addVariant(ProductVariantRequest request){

        if (productVariantRepository.existsByProductIdAndSizeIdAndColorId(
                request.productId(), request.sizeId(), request.colorId())) {
            throw new AlreadyExistException("Variant already exists");
        }


        Product product = productRepository.findProductById(request.productId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        Size size = sizeRepository.findById(request.sizeId())
                .orElseThrow(() -> new NotFoundException("Size not found"));

        Color color = colorRepository.findById(request.colorId())
                .orElseThrow(() -> new NotFoundException("Color not found"));

        if (productVariantRepository.existsBySku(request.sku())) {
            throw new AlreadyExistException("SKU already exists");
        }

       ProductVariant savedProductVariant = toProductVariant(request,product,color,size);

        return mapToVariantResponse( productVariantRepository.save(savedProductVariant));

   }
    @Override
    @Transactional
    public ProductVariantResponse updateVariant(Long id, ProductVariantRequest request) {

        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Variant not found"));

        if (productVariantRepository.existsByProductIdAndSizeIdAndColorId(
                request.productId(), request.sizeId(), request.colorId())) {

            boolean sameCombination =
                    variant.getProduct().getId().equals(request.productId()) &&
                            variant.getSize().getId().equals(request.sizeId()) &&
                            variant.getColor().getId().equals(request.colorId());

            if (!sameCombination) {
                throw new AlreadyExistException("Variant already exists");
            }
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        Size size = sizeRepository.findById(request.sizeId())
                .orElseThrow(() -> new NotFoundException("Size not found"));

        Color color = colorRepository.findById(request.colorId())
                .orElseThrow(() -> new NotFoundException("Color not found"));

        updateProductVariant(variant,product,request,color,size);

        return mapToVariantResponse(productVariantRepository.save(variant));
    }



    @Override
    public List<ProductVariantResponse> getAll() {
        return productVariantRepository.findAll()
                .stream()
                .map(ProductVariantMapper::mapToVariantResponse)
                .toList();
    }
    @Override
    @Transactional
    public ProductVariantResponse getById(Long id) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Variant not found"));

        return mapToVariantResponse(variant);
    }
    @Override
    @Transactional
    public List<ProductVariantResponse> getByProduct(Long productId) {
        return productVariantRepository.findByProductId(productId)
                .stream()
                .map(ProductVariantMapper::mapToVariantResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductVariantResponse updateStock(Long variantId, UpdateStockRequest request) {

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("Product variant not found"));

        variant.setStock(request.stock());

        ProductVariant savedVariant = productVariantRepository.save(variant);

        return mapToVariantResponse(savedVariant);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Variant not found"));

        productVariantRepository.delete(variant);
    }
}
