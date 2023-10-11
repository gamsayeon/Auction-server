package com.example.auction_server.service;

import com.example.auction_server.model.Bid;

public interface MQService {
    void enqueueMassage(Bid bid);
}
