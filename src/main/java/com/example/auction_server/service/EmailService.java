package com.example.auction_server.service;

public interface EmailService {
    String putCacheToken(String token, String userId);

    void sendToUser(String token, String email);
}
