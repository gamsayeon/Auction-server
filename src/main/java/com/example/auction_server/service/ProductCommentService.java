package com.example.auction_server.service;

import com.example.auction_server.dto.ProductCommentDTO;

public interface ProductCommentService {

    ProductCommentDTO registerProduct(Long userId, Long productId, ProductCommentDTO productCommentDTO);
}
