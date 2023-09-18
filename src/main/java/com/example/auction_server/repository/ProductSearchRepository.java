package com.example.auction_server.repository;

import com.example.auction_server.model.Product;

import java.util.List;

public interface ProductSearchRepository {
    List<Product> searchProducts(String productName, Long saleId, Long categoryId, String explanation, int page, int pageSize);

    int countBySearchProducts(String productName, Long saleId, Long categoryId, String explanation);

}
