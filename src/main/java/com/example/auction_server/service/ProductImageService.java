package com.example.auction_server.service;

import com.example.auction_server.dto.ProductDTO;
import com.example.auction_server.dto.ProductImageDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductImageService {
    List<ProductImageDTO> registerProductImage(ProductDTO productDTO, Long productId);

    List<ProductImageDTO> selectProductImage(Long productId);

    void deleteProductImage(Long productId);
}
