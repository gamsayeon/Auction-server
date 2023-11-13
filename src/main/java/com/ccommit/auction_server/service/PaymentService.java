package com.ccommit.auction_server.service;

import com.ccommit.auction_server.model.toss.PaymentApproveResponse;
import com.ccommit.auction_server.model.toss.PaymentRefundsResponse;
import com.ccommit.auction_server.model.toss.PaymentSelectResponse;

import java.util.List;

public interface PaymentService {

    void createPayment(int price, String productName, Long productId);

    PaymentApproveResponse approvePayment(String orderNo);

    List<PaymentSelectResponse> selectPayment(Long userId);

    PaymentRefundsResponse refundsPayment(String payToken);

    void closePayment(String orderId);
}
