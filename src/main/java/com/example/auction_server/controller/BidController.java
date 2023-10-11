package com.example.auction_server.controller;

import com.example.auction_server.aop.LoginCheck;
import com.example.auction_server.dto.BidDTO;
import com.example.auction_server.model.CommonResponse;
import com.example.auction_server.service.serviceImpl.BidServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Bid API", description = "Bid 관련 API")
public class BidController {
    private final BidServiceImpl bidService;

    private final Logger logger = LogManager.getLogger(BidController.class);

    @PostMapping("/{productId}")
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "BID_FAILED_NOT_START", description = "경매가 시작하지 않음", content = @Content),
            @ApiResponse(responseCode = "BID_INPUT_MISMATCH", description = "입찰 값을 잘못입력시", content = @Content),
            @ApiResponse(responseCode = "200", description = "입찰 등록 성공", content = @Content(schema = @Schema(implementation = BidDTO.class)))
    })
    @Operation(summary = "Bid 등록",
            description = "구매자가 입찰을 합니다. 하단의 BidDTO 참고",
            method = "POST",
            tags = "Bid API",
            operationId = "Register Bid")
    @Parameter(name = "productId", description = "입찰할 상품 식별자", example = "1")
    public ResponseEntity<CommonResponse<BidDTO>> registerBid(@Parameter(hidden = true) Long loginId, @PathVariable("productId") Long productId,
                                                              @RequestBody @Valid BidDTO bidDTO, HttpServletRequest request) {
        logger.info("경매에 입찰합니다.");
        CommonResponse<BidDTO> response = new CommonResponse<>("SUCCESS", "경매에 입찰했습니다.",
                request.getRequestURI(), bidService.registerBid(loginId, productId, bidDTO));
        return ResponseEntity.ok(response);
    }
}
