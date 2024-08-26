package com.ccommit.auction_server.controller;

import com.ccommit.auction_server.aop.LoginCheck;
import com.ccommit.auction_server.dto.BidDTO;
import com.ccommit.auction_server.model.CommonResponse;
import com.ccommit.auction_server.service.MQService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/enqueue")
@RestController
@Log4j2
@RequiredArgsConstructor
public class EnqueueController {
    private final MQService rabbitMQService;
    private static final Logger logger = LogManager.getLogger(EnqueueController.class);

    @PostMapping("/bids/products/{productId}")
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "BID_FAILED_NOT_START : 경매가 시작하지 않음<br>" +
                    "BID_INPUT_MISMATCH : 입찰 값을 잘못입력시", content = @Content),
            @ApiResponse(responseCode = "200", description = "입찰 등록 성공", content = @Content(schema = @Schema(implementation = BidDTO.class)))
    })
    @Parameter(name = "productId", description = "입찰할 상품 식별자", example = "1")
    public ResponseEntity<CommonResponse<BidDTO>> registerBid(@Parameter(hidden = true) Long loginId, @PathVariable("productId") Long productId,
                                                              @RequestBody @Valid BidDTO bidDTO, HttpServletRequest request) {
        logger.info("경매에 입찰합니다.");
        CommonResponse<BidDTO> response = new CommonResponse<>("SUCCESS", "경매에 입찰했습니다.",
                request.getRequestURI(), rabbitMQService.enqueueMassage(loginId, productId, bidDTO));
        return ResponseEntity.ok(response);
    }
}
