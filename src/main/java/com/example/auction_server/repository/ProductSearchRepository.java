package com.example.auction_server.repository;

import com.example.auction_server.model.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository {
    List<Product> searchProducts(String productName, Long saleId, Long categoryId, String explanation, int page, int pageSize);
}
