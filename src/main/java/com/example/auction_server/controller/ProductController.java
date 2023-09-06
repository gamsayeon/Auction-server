package com.example.auction_server.controller;

import com.example.auction_server.aop.LoginCheck;
import com.example.auction_server.dto.ProductDTO;
import com.example.auction_server.model.CommonResponse;
import com.example.auction_server.service.serviceImpl.ProductServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final Logger logger = LogManager.getLogger(ProductController.class);

    private final ProductServiceImpl productService;

    public ProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @PostMapping
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    public ResponseEntity<CommonResponse<ProductDTO>> registerProduct(Long loginId, @RequestBody @Valid ProductDTO productDTO,
                                                                      HttpServletRequest request) {
        logger.debug("상품을 등록합니다.");
        CommonResponse<ProductDTO> response = new CommonResponse<>("SUCCESS", "상품을 등록했습니다.",
                request.getRequestURI(), productService.registerProduct(loginId, productDTO));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<CommonResponse<ProductDTO>> selectProduct(@PathVariable("productId") Long productId,
                                                                    HttpServletRequest request) {
        logger.debug("상품을 조회합니다.");
        CommonResponse<ProductDTO> response = new CommonResponse<>("SUCCESS", "상품을 조회했습니다.",
                request.getRequestURI(), productService.selectProduct(productId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    public ResponseEntity<CommonResponse<List<ProductDTO>>> selectProductForUser(Long loginId, HttpServletRequest request) {
        logger.debug("상품을 조회합니다.");
        CommonResponse<List<ProductDTO>> response = new CommonResponse<>("SUCCESS", "상품을 조회했습니다.",
                request.getRequestURI(), productService.selectProductForUser(loginId));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{productId}")
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    public ResponseEntity<CommonResponse<ProductDTO>> updateProduct(Long loginId, @PathVariable("productId") Long productId,
                                                                    @RequestBody @Valid ProductDTO productDTO,
                                                                    HttpServletRequest request) {
        logger.debug("상품을 수정합니다.");
        CommonResponse<ProductDTO> response = new CommonResponse<>("SUCCESS", "상품을 수정했습니다.",
                request.getRequestURI(), productService.updateProduct(loginId, productId, productDTO));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/withdraw/{productId}")
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    public ResponseEntity<CommonResponse<String>> deleteProduct(Long loginId, @PathVariable("productId") Long productId,
                                                                HttpServletRequest request) {
        logger.debug("상품을 삭제합니다.");
        productService.deleteProduct(loginId, productId);
        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "상품을 삭제했습니다.",
                request.getRequestURI(), productId + " 정상적으로 상품이 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }

    @Scheduled(cron = "${auction.time.interval}")
    public void updateProductAuctionStatus() {
        logger.debug("경매상태값을 수정합니다.");
        productService.updateProductStatus();
    }

}
