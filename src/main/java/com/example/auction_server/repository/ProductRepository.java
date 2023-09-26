package com.example.auction_server.repository;

import com.example.auction_server.enums.ProductStatus;
import com.example.auction_server.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductSearchRepository {
    Product findByProductId(Long productId);

    List<Product> findBySaleId(Long saleId);

    int deleteBySaleIdAndProductId(Long saleId, Long productId);

    List<Product> findByProductStatus(ProductStatus productStatus);

}
