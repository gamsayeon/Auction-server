package com.ccommit.auction_server.controller;

import com.ccommit.auction_server.aop.LoginCheck;
import com.ccommit.auction_server.enums.PaymentStatus;
import com.ccommit.auction_server.model.CommonResponse;
import com.ccommit.auction_server.model.toss.PaymentApproveResponse;
import com.ccommit.auction_server.model.toss.PaymentRefundsResponse;
import com.ccommit.auction_server.model.toss.PaymentSelectResponse;
import com.ccommit.auction_server.service.serviceImpl.TossPaymentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final TossPaymentServiceImpl tossPaymentService;

    @GetMapping("/close")
    public ResponseEntity<CommonResponse<String>> closePayment(@RequestParam(name = "orderNo") String orderId,
                                                               HttpServletRequest request) {
        tossPaymentService.closePayment(orderId);
        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "결제 취소에 성공했습니다.",
                request.getRequestURI());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/approve")
    public ResponseEntity<CommonResponse<PaymentApproveResponse>> approvePayment(
            @RequestParam(name = "orderNo") String orderNo,
            @RequestParam(name = "status") PaymentStatus status,
            @RequestParam(name = "payMethod") String payMethod,
            @RequestParam(name = "bankCode") String bankCode,
            HttpServletRequest request) {
        CommonResponse<PaymentApproveResponse> response = new CommonResponse<>("SUCCESS", "결제에 성공했습니다.",
                request.getRequestURI(), tossPaymentService.approvePayment(orderNo));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    public ResponseEntity<CommonResponse<List<PaymentSelectResponse>>> selectPayment(Long loginId,
                                                                                     HttpServletRequest request) {
        CommonResponse<List<PaymentSelectResponse>> response = new CommonResponse<>("SUCCESS", "결제 내역을 조회합니다.",
                request.getRequestURI(), tossPaymentService.selectPayment(loginId));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderNo}/refunds")
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    public ResponseEntity<CommonResponse<PaymentRefundsResponse>> refundsPayment(Long loginId,
                                                                                 @RequestBody String payToken,
                                                                                 HttpServletRequest request) {
        CommonResponse<PaymentRefundsResponse> response = new CommonResponse<>("SUCCESS", "환불을 진행합니다.",
                request.getRequestURI(), tossPaymentService.refundsPayment(payToken));
        return ResponseEntity.ok(response);
    }
}
