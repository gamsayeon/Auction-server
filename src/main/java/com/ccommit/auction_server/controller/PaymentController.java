package com.ccommit.auction_server.controller;

import com.ccommit.auction_server.model.CommonResponse;
import com.ccommit.auction_server.model.PaymentApproveResponse;
import com.ccommit.auction_server.service.serviceImpl.TossPaymentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final TossPaymentServiceImpl tossPaymentService;

    @GetMapping("/approve")
    public ResponseEntity<CommonResponse<PaymentApproveResponse>> paymentApprove(
            @RequestParam(name = "orderNo") String orderNo,
            @RequestParam(name = "status") String status,
            @RequestParam(name = "orderNo") String orderNumber,
            @RequestParam(name = "payMethod") String paymentMethod,
            @RequestParam(name = "bankCode") String bankCode,
            HttpServletRequest request) {
        CommonResponse<PaymentApproveResponse> response = new CommonResponse<>("SUCCESS", "결제에 성공했습니다.",
                request.getRequestURI(), tossPaymentService.approvePayment(orderNo));
        return ResponseEntity.ok(response);
    }

}
