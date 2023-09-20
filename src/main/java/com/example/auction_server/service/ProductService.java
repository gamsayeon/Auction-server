package com.example.auction_server.service;

import com.example.auction_server.dto.ProductDTO;
import com.example.auction_server.enums.ProductSortOrder;

import java.util.List;

public interface ProductService {
    ProductDTO registerProduct(Long id, ProductDTO productDTO);

    ProductDTO selectProduct(Long productId);

    List<ProductDTO> selectProductForUser(Long saleUserId);

    ProductDTO updateProduct(Long saleUserId, Long productId, ProductDTO productDTO);

    void deleteProduct(Long saleUserId, Long productId);

    void updateProductStatus();

    List<ProductDTO> findByKeyword(String productName, Long saleUserId, Long categoryId, String explanation, ProductSortOrder sortOrder);
}
