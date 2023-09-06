package com.example.auction_server.controller;

import com.example.auction_server.aop.LoginCheck;
import com.example.auction_server.dto.ProductCommentDTO;
import com.example.auction_server.model.CommonResponse;
import com.example.auction_server.service.serviceImpl.ProductCommentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product-comments")
public class ProductCommentController {
    private final Logger logger = LogManager.getLogger(ProductCommentController.class);

    private final ProductCommentServiceImpl productCommentService;

    public ProductCommentController(ProductCommentServiceImpl productCommentService) {
        this.productCommentService = productCommentService;
    }

    @PostMapping("/{productId}")
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    public ResponseEntity<CommonResponse<ProductCommentDTO>> registerProductComment(Long loginId, @PathVariable("productId") Long productId,
                                                                                    @RequestBody @Valid ProductCommentDTO productCommentDTO,
                                                                                    HttpServletRequest request) {
        logger.info("상품 댓글을 등록합니다.");
        CommonResponse<ProductCommentDTO> response = new CommonResponse<>("SUCCESS", "상품에 댓글을 추가했습니다.",
                request.getRequestURI(), productCommentService.registerProduct(loginId, productId, productCommentDTO));
        return ResponseEntity.ok(response);
    }
}
