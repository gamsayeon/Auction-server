package com.ccommit.auction_server.service;

import com.ccommit.auction_server.dto.BidDTO;
import com.ccommit.auction_server.model.Bid;

import java.util.List;

public interface BidService {
    List<BidDTO> selectBidByUserId(Long buyerId, Long productId);

    List<BidDTO> selectBidByProduct(Long productId);

    Bid saveBid(Bid bid);

    void validBidPrice(Long productId, Integer newPrice);
}
