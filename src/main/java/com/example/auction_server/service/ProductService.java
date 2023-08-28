package com.example.auction_server.service;

import com.example.auction_server.dto.ProductDTO;
import org.springframework.transaction.annotation.Transactional;

public interface ProductService {
    ProductDTO registerProduct(Long id, ProductDTO productDTO);

    ProductDTO selectProduct(Long productId);

    ProductDTO updateProduct(Long saleUserId, Long productId, ProductDTO productDTO);

    void deleteProduct(Long saleUserId, Long productId);
}
