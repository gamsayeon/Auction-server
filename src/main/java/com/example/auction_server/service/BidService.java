package com.example.auction_server.service;

import com.example.auction_server.dto.BidDTO;

public interface BidService {
    BidDTO registerBid(Long buyerUserId, Long productId, BidDTO bidDTO);
}
