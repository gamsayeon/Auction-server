package com.example.auction_server.repository;

import com.example.auction_server.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByProductId(Long productId);

    int deleteBySaleUserIdAndProductId(Long saleUserId, Long productId);
}
