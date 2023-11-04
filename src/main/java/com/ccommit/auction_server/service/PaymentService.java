package com.ccommit.auction_server.service;

import com.ccommit.auction_server.model.PaymentApproveResponse;
import com.ccommit.auction_server.model.PaymentRequest;

public interface PaymentService {
    void createPayment(PaymentRequest paymentRequest, Long productId);

    PaymentApproveResponse approvePayment(String orderNo);
}
