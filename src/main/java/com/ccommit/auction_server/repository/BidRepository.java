package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    Bid findTopByProductIdOrderByPriceDesc(Long productId);

    Long countByProductId(Long productId);

    List<Bid> findByBuyerId(Long buyerId);

    List<Bid> findByBuyerIdAndProductId(Long buyerId, Long productId);
}