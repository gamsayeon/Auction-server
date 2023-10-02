package com.example.auction_server.service;

import com.example.auction_server.dto.BidDTO;

public interface BidService {
    BidDTO registerBid(Long buyerId, Long productId, BidDTO bidDTO);
}
