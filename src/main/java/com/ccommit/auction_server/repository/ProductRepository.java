package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.enums.ProductStatus;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.model.ProductCategoryHighestBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByProductId(Long productId);

    List<Product> findBySaleId(Long saleId);

    int deleteByProductId(Long productId);

    @Query("SELECT p FROM product p WHERE p.productStatus = :productStatusValue1 OR p.productStatus = :productStatusValue2")
    List<Product> findByProductStatus(@Param("productStatusValue1") ProductStatus productStatusValue1,
                                      @Param("productStatusValue2") ProductStatus productStatusValue2);

    boolean existsByProductId(Long productId);

    @Query("SELECT new com.ccommit.auction_server.model.ProductCategoryHighestBid(p, c, b) " +
            "FROM product p " +
            "JOIN p.category c " +
            "LEFT JOIN bid b ON b.product.productId = p.productId " +
            "WHERE b.price = (SELECT MAX(b1.price) " +
            "FROM bid b1 " +
            "WHERE b1.product.productId = p.productId) " +
            "AND p.productId = :productId")
    Optional<ProductCategoryHighestBid> findByProductIdWithCategoryAndHighestBid(@Param("productId") Long productId);
}