package com.example.auction_server.service;

import com.example.auction_server.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    ProductDTO registerProduct(Long id, ProductDTO productDTO);

    ProductDTO selectProduct(Long productId);

    List<ProductDTO> selectProductForUser(Long saleUserId);

    ProductDTO updateProduct(Long saleUserId, Long productId, ProductDTO productDTO);

    void deleteProduct(Long saleUserId, Long productId);

    void updateProductStatus();

}
