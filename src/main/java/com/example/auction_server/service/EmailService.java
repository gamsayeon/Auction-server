package com.example.auction_server.service;

import org.springframework.cache.annotation.Cacheable;

public interface EmailService {
    String putCacheToken(String token, String userId);

    void sendTokenToUser(String token, String email);

    void notifyAuction(String recipientEmail, String subject, String message);
}
