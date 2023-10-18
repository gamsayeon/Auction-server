package com.example.auction_server.service;

import com.example.auction_server.dto.BidDTO;

import java.util.List;

public interface BidService {
    BidDTO registerBid(Long buyerId, Long productId, BidDTO bidDTO);

    List<BidDTO> selectBidByBuyerId(Long buyerId, Long productId);

    List<BidDTO> selectBidBySaleId(Long saleId, Long productId);
}
