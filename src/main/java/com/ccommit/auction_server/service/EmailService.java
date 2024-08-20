package com.ccommit.auction_server.service;

import com.amazonaws.services.simpleemail.model.Message;
import com.ccommit.auction_server.model.Bid;
import com.ccommit.auction_server.model.Product;

public interface EmailService {
    void sendTokenToUser(String token, String email);

    void notifyAuction(String recipientEmail, String subject, String message);

    String verifyEmail(String token);

    void notifyAuctionSuccess(Bid bid, Product product);
}
