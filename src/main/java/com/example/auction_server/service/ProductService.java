package com.example.auction_server.service;

import com.example.auction_server.dto.ProductDTO;

public interface ProductService {
    ProductDTO registerProduct(Long id, ProductDTO productDTO);

    ProductDTO selectProduct(Long productId);

}
