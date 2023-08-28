package com.example.auction_server.controller;

import com.example.auction_server.aop.LoginCheck;
import com.example.auction_server.dto.ProductDTO;
import com.example.auction_server.service.serviceImpl.ProductServiceImpl;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final Logger logger = LogManager.getLogger(ProductController.class);

    private final ProductServiceImpl productService;

    public ProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @PostMapping
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    public ResponseEntity<ProductDTO> registerProduct(Long loginId, @RequestBody @Valid ProductDTO productDTO) {
        logger.debug("상품을 등록합니다.");
        ProductDTO resultProductDTO = productService.registerProduct(loginId, productDTO);
        return ResponseEntity.ok(resultProductDTO);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> selectProduct(@PathVariable("productId") Long productId) {
        logger.debug("상품을 조회합니다.");
        ProductDTO resultProductDTO = productService.selectProduct(productId);
        return ResponseEntity.ok(resultProductDTO);
    }

    @PatchMapping("/{productId}")
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    public ResponseEntity<ProductDTO> updateProduct(Long loginId, @PathVariable("productId") Long productId,
                                                    @RequestBody @Valid ProductDTO productDTO) {
        logger.debug("상품을 수정합니다.");
        ProductDTO resultProductDTO = productService.updateProduct(loginId, productId, productDTO);
        return ResponseEntity.ok(resultProductDTO);
    }

    @DeleteMapping("/withdraw/{productId}")
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    public ResponseEntity<String> deleteProduct(Long loginId, @PathVariable("productId") Long productId) {
        logger.debug("상품을 삭제합니다.");
        productService.deleteProduct(loginId, productId);
        return ResponseEntity.ok(productId + " 정상적으로 상품이 삭제되었습니다.");
    }

}
