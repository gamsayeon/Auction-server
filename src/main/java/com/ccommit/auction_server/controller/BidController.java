package com.ccommit.auction_server.controller;

import com.ccommit.auction_server.aop.LoginCheck;
import com.ccommit.auction_server.dto.BidDTO;
import com.ccommit.auction_server.model.CommonResponse;
import com.ccommit.auction_server.service.serviceImpl.BidServiceImpl;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/bids")
@RestController
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Bid API", description = "Bid 관련 API")
public class BidController {
    private final BidServiceImpl bidService;

    private final Logger logger = LogManager.getLogger(BidController.class);

    @GetMapping("/histories")
    @LoginCheck(types = LoginCheck.LoginType.USER)
    public ResponseEntity<CommonResponse<List<BidDTO>>> selectBidByUser(@Parameter(hidden = true) Long loginId,
                                                                        @RequestParam(value = "productId", required = false) Long productId,
                                                                        HttpServletRequest request) {
        logger.info("경매 이력을 조회합니다.");
        CommonResponse<List<BidDTO>> response = new CommonResponse<>("SUCCESS", "경매를 조회했습니다.",
                request.getRequestURI(), bidService.selectBidByUserId(loginId, productId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/histories/products/{productId}")
    public ResponseEntity<CommonResponse<List<BidDTO>>> selectBidByProduct(@PathVariable("productId") Long productId,
                                                                           HttpServletRequest request) {
        logger.info("판매자의 경매 이력을 조회합니다.");
        CommonResponse<List<BidDTO>> response = new CommonResponse<>("SUCCESS", "경매를 조회했습니다.",
                request.getRequestURI(), bidService.selectBidByProduct(productId));
        return ResponseEntity.ok(response);
    }
}
