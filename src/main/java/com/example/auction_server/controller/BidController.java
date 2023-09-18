package com.example.auction_server.controller;

import com.example.auction_server.aop.LoginCheck;
import com.example.auction_server.dto.BidDTO;
import com.example.auction_server.model.CommonResponse;
import com.example.auction_server.service.serviceImpl.BidServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/bids")
@RestController
@Log4j2
@RequiredArgsConstructor
public class BidController {
    private final BidServiceImpl bidService;

    private final Logger logger = LogManager.getLogger(BidController.class);

    @PostMapping("/{productId}")
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    public ResponseEntity<CommonResponse<BidDTO>> registerBid(Long loginId, @PathVariable("productId") Long productId,
                                                              @RequestBody @Valid BidDTO bidDTO, HttpServletRequest request) {
        logger.info("경매에 입찰합니다.");
        CommonResponse<BidDTO> response = new CommonResponse<>("SUCCESS", "경매에 입찰했습니다.",
                request.getRequestURI(), bidService.registerBid(loginId, productId, bidDTO));
        return ResponseEntity.ok(response);
    }
}
