package com.example.auction_server.repository;

import com.example.auction_server.model.Product;

import java.util.List;

public interface ProductSearchRepository {
    List<Product> searchProducts(String postName, Long saleUserId, Long categoryId, String explanation);
}
