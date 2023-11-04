package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.enums.PaymentStatus;
import com.ccommit.auction_server.model.*;
import com.ccommit.auction_server.projection.UserProjection;
import com.ccommit.auction_server.repository.BidRepository;
import com.ccommit.auction_server.repository.PaymentRepository;
import com.ccommit.auction_server.repository.UserRepository;
import com.ccommit.auction_server.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TossPaymentServiceImpl implements PaymentService {
    @Value("${test.toss.api.key}")
    private String apiKey;

    private final EmailServiceImpl emailService;
    private final BidRepository bidRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Override
    public void createPayment(PaymentRequest paymentRequest, Long productId) {
        URL url = null;
        URLConnection connection = null;
        StringBuilder responseBody = new StringBuilder();
        String uuid = UUID.randomUUID().toString();
        String orderNo = uuid.replaceAll("-", "").substring(0, 10);
        try {
            url = new URL("https://pay.toss.im/api/v2/payments");
            connection = url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            paymentRequest.setOrderNo(orderNo);
            paymentRequest.setApiKey(apiKey);
            paymentRequest.setRetUrl("http://localhost:8080/payments/approve?orderno=" + orderNo);
            paymentRequest.setRetCancelUrl("http://localhost:8080/close");
            paymentRequest.setAutoExecute(false);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(paymentRequest);

            BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());

            bos.write(jsonStr.getBytes());
            bos.flush();
            bos.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line = null;
            while ((line = br.readLine()) != null) {
                responseBody.append(line);
            }
            br.close();
        } catch (Exception e) {
            responseBody.append(e);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> successMap = objectMapper.readValue(responseBody.toString(), Map.class);

            // "code" 키를 사용하여 성공과 실패를 분리
            int code = (int) successMap.get("code");

            if (code == -1) {
                // 실패 응답 처리
                PaymentResponse failureResponse = objectMapper.readValue(responseBody.toString(), PaymentResponse.class);
                System.out.println(failureResponse);
            } else {
                // 성공 응답 처리
                PaymentResponse successResponse = objectMapper.readValue(responseBody.toString(), PaymentResponse.class);
                Long buyerId = bidRepository.findTopByProductIdOrderByPriceDesc(productId).getBuyerId();
                UserProjection email =  userRepository.findUserProjectionById(buyerId);
                emailService.notifyAuction(email.getEmail(), "결제 요청", successResponse.getCheckoutPage());
                Payment payment = Payment.builder()
                        .orderId(orderNo)
                        .paymentId(successResponse.getPayToken())
                        .paymentAmount(paymentRequest.getAmount())
                        .build();
                paymentRepository.save(payment);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PaymentApproveResponse approvePayment(String orderNo) {
        URL url = null;
        URLConnection connection = null;
        StringBuilder responseBody = new StringBuilder();
        try {
            url = new URL("https://pay.toss.im/api/v2/execute");
            connection = url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            Payment payment = paymentRepository.findByOrderId(orderNo);

            PaymentApproveRequest paymentApproveRequest = PaymentApproveRequest.builder()
                    .apiKey(apiKey)
                    .payToken(payment.getPaymentId())
                    .build();


            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(paymentApproveRequest);

            BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());

            bos.write(jsonStr.getBytes());
            bos.flush();
            bos.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line = null;
            while ((line = br.readLine()) != null) {
                responseBody.append(line);
            }
            br.close();
        } catch (Exception e){
            responseBody.append(e);
        }
        PaymentApproveResponse paymentApproveResponse = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            paymentApproveResponse = objectMapper.readValue(responseBody.toString(), PaymentApproveResponse.class);
            if(paymentApproveResponse.getCode() == 0){
                Payment payment = paymentRepository.findByOrderId(paymentApproveResponse.getOrderNo());
                payment.setPaymentDate(LocalDateTime.now());
                payment.setPaymentMethod(paymentApproveResponse.getPayMethod());
                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(payment);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return paymentApproveResponse;
    }
}
