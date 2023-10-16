package com.example.auction_server.repository;

import com.example.auction_server.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    @Query("SELECT MAX(b.price) FROM bid b WHERE b.productId = :productId")
    Integer findMaxPriceByProductId(@Param("productId") Long productId);

    Long countByProductId(Long productId);
}