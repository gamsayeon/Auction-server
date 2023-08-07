package com.example.auction_server.service;

import org.springframework.cache.annotation.Cacheable;

public interface EmailService {
    String generateToken();

    String putCacheToken(String token, String userId);
    void sendToUser(String token, String email);
}
