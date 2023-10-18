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

import java.util.List;

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
            @ApiResponse(responseCode = "400", description = "BID_FAILED_NOT_START : 경매가 시작하지 않음<br>" +
                    "BID_INPUT_MISMATCH : 입찰 값을 잘못입력시", content = @Content),
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

    @GetMapping("/buyer")
    @LoginCheck(types = LoginCheck.LoginType.USER)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "PRODUCT_NOT_MATCH_ID : 해당하는 상품을 찾지 못함<br>" +
                    "BID_NULL_DATA : 아직 경매 이력이 없을시<br>" +
                    "COMMON_ACCESS_DENIED : 해당하는 상품의 경매이력의 조회 권한이 없음", content = @Content),
            @ApiResponse(responseCode = "200", description = "입찰 이력 조회 성공(구매자)", content = @Content(schema = @Schema(implementation = BidDTO.class)))
    })
    @Operation(summary = "Bid 이력 조회(구매자)",
            description = "구매자가 자신의 경매 이력을 조회 합니다.]",
            method = "GET",
            tags = "Bid API",
            operationId = "Select Bid from Buyer")
    public ResponseEntity<CommonResponse<List<BidDTO>>> selectBidFromBuyer(@Parameter(hidden = true) Long loginId,
                                                                           @RequestParam(value = "productId", required = false) Long productId,
                                                                           HttpServletRequest request) {
        logger.info("구매자의 경매 이력을 조회합니다.");
        CommonResponse<List<BidDTO>> response = new CommonResponse<>("SUCCESS", "경매를 조회했습니다.",
                request.getRequestURI(), bidService.selectBidByBuyerId(loginId, productId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("sale/{productId}")
    @LoginCheck(types = LoginCheck.LoginType.USER)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "PRODUCT_NOT_MATCH_ID : 해당하는 상품을 찾지 못함<br>" +
                    "BID_NULL_DATA : 아직 경매 이력이 없을시", content = @Content),
            @ApiResponse(responseCode = "200", description = "입찰 이력 조회 성공(판매자)", content = @Content(schema = @Schema(implementation = BidDTO.class)))
    })
    @Operation(summary = "Bid 이력 조회(판매자)",
            description = "판매자가 자신의 경매 이력을 조회 합니다.]",
            method = "GET",
            tags = "Bid API",
            operationId = "Select Bid from Sale")
    @Parameter(name = "productId", description = "경매 이력을 조회할 상품 식별자", example = "1")
    public ResponseEntity<CommonResponse<List<BidDTO>>> selectBidFromSale(@Parameter(hidden = true) Long loginId, @PathVariable("productId") Long productId,
                                                                          HttpServletRequest request) {
        logger.info("판맨자의 경매 이력을 조회합니다.");
        CommonResponse<List<BidDTO>> response = new CommonResponse<>("SUCCESS", "경매를 조회했습니다.",
                request.getRequestURI(), bidService.selectBidBySaleId(loginId, productId));
        return ResponseEntity.ok(response);
    }
}
