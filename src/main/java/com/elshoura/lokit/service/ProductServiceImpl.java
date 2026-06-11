package com.elshoura.lokit.service;

import com.elshoura.lokit.errors.exception.NotFoundException;
import com.elshoura.lokit.models.dto.request.ProductRequest;
import com.elshoura.lokit.models.dto.response.ProductResponse;
import com.elshoura.lokit.models.entitys.*;
import com.elshoura.lokit.repository.*;
import com.elshoura.lokit.utils.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.elshoura.lokit.utils.mapper.ProductMapper.*;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

private final ProductRepository productRepository;
private final BrandRepository brandRepository;
private final CategoryRepository categoryRepository;
private final MaterialRepository materialRepository;
private final DepartmentRepository departmentRepository;

@Override
public ProductResponse addProduct(ProductRequest productRequest){

    Brand brand = brandRepository.findById(productRequest.brandId())
            .orElseThrow(()-> new NotFoundException("Brand not found"));

    Category category = categoryRepository.findById(productRequest.categoryId())
            .orElseThrow(()-> new NotFoundException("Category not found"));

    Department department = departmentRepository.findById(productRequest.departmentId())
            .orElseThrow(()-> new NotFoundException("Department not found"));

    Material material = materialRepository.findById(productRequest.materialId())
            .orElseThrow(()-> new NotFoundException("Material not found"));


    Product product = toProduct(productRequest,brand,category,department ,material);

    Product savedProduct = productRepository.save(product);

    return mapToProductResponse(savedProduct);

    }
    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest){

    Product product = productRepository.findById(id)
            .orElseThrow(()-> new NotFoundException("Product not found"));

    Brand brand = brandRepository.findById(productRequest.brandId())
            .orElseThrow(()-> new NotFoundException("Brand not found"));


        Category category = categoryRepository.findById(productRequest.categoryId())
                .orElseThrow(()-> new NotFoundException("Category not found"));

        Department department = departmentRepository.findById(productRequest.departmentId())
                .orElseThrow(()-> new NotFoundException("Department not found"));

        Material material = materialRepository.findById(productRequest.materialId())
                .orElseThrow(()-> new NotFoundException("Material not found"));


        update(product,productRequest,brand,category,department ,material);

     return mapToProductResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(){
    return productRepository.findAll().stream()
            .map(ProductMapper::mapToProductResponse)
            .toList();
    }

    @Override
    public ProductResponse getProductById(Long id){
      Product product =  productRepository.findById(id).
                orElseThrow(()-> new NotFoundException("Product not found"));

        return mapToProductResponse(product);

    }
   @Override
    public void deleteProduct(Long id){
    Product product =  productRepository.findById(id)
            .orElseThrow(()-> new NotFoundException("Product not found"));

    productRepository.delete(product);
   }
}